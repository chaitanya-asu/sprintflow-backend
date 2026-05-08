package com.sprintflow.controller;

import com.sprintflow.dto.ApiResponseDTO;
import com.sprintflow.dto.ChatMessageDTO;
import com.sprintflow.entity.ChatMessage;
import com.sprintflow.entity.User;
import com.sprintflow.repository.ChatGroupRepository;
import com.sprintflow.repository.ChatMessageRepository;
import com.sprintflow.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages", description = "Private real-time messaging. WebSocket: ws://localhost:8080/ws")
public class MessageController {

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private ChatMessageRepository chatMessageRepository;
    @Autowired private UserRepository        userRepository;
    @Autowired private ChatGroupRepository   chatGroupRepository;

    private final Map<String, String> presenceMap = new ConcurrentHashMap<>();

    // ── STOMP: send private message ───────────────────────────
    @MessageMapping("/chat.send")
    public void send(@Payload ChatMessageDTO.SendRequest req,
                     SimpMessageHeaderAccessor headerAccessor) {
        Principal principal = headerAccessor.getUser();
        if (principal == null) return;
        if (req.getRecipientEmail() == null || req.getContent() == null
                || req.getContent().isBlank()) return;

        String senderEmail    = principal.getName().toLowerCase();
        String recipientEmail = req.getRecipientEmail() != null ? req.getRecipientEmail().trim().toLowerCase() : null;
        
        if (recipientEmail != null && senderEmail.equals(recipientEmail)) return;

        User sender = userRepository.findByEmail(senderEmail).orElse(null);
        if (sender == null) return;

        // Persist — mark delivered immediately (message reached the server)
        ChatMessage saved = new ChatMessage(
                senderEmail, sender.getName(),
                sender.getRole() != null ? sender.getRole().name() : "UNKNOWN",
                recipientEmail, req.getContent().trim());
        saved.setDelivered(true);
        chatMessageRepository.save(saved);

        ChatMessageDTO.Payload payload = toPayload(saved);

        // Deliver to recipient's private queue
        if (req.getRecipientEmail() != null) {
            messagingTemplate.convertAndSendToUser(recipientEmail, "/queue/messages", payload);
        } else if (req.getRecipientGroupId() != null) {
            // Group message — send to group topic
            messagingTemplate.convertAndSend("/topic/group." + req.getRecipientGroupId(), payload);
        }
        
        // Echo to sender so their UI updates
        messagingTemplate.convertAndSendToUser(senderEmail, "/queue/messages", payload);
    }

    @MessageMapping("/chat.group.send")
    public void sendGroup(@Payload ChatMessageDTO.SendRequest req,
                          SimpMessageHeaderAccessor headerAccessor) {
        Principal principal = headerAccessor.getUser();
        if (principal == null || req.getRecipientGroupId() == null || req.getContent() == null || req.getContent().isBlank()) return;

        String senderEmail = principal.getName().toLowerCase();
        User sender = userRepository.findByEmail(senderEmail).orElse(null);
        if (sender == null) return;

        ChatMessage saved = new ChatMessage(
                senderEmail, sender.getName(),
                sender.getRole() != null ? sender.getRole().name() : "UNKNOWN",
                null, req.getContent().trim());
        saved.setRecipientGroupId(req.getRecipientGroupId());
        saved.setDelivered(true);
        chatMessageRepository.save(saved);

        ChatMessageDTO.Payload payload = toPayload(saved);
        messagingTemplate.convertAndSend("/topic/group." + req.getRecipientGroupId(), payload);
        // Also send to sender's private queue for UI sync if they are not subscribed to topic yet
        messagingTemplate.convertAndSendToUser(senderEmail, "/queue/messages", payload);
    }

    // ── STOMP: mark messages as read (seen receipt) ───────────
    // Client publishes when the recipient opens the conversation.
    // Payload: { senderEmail: "...", conversationWith: "..." }
    @MessageMapping("/chat.read")
    public void markRead(@Payload Map<String, String> body,
                         SimpMessageHeaderAccessor headerAccessor) {
        Principal principal = headerAccessor.getUser();
        if (principal == null) return;

        String readerEmail = principal.getName().toLowerCase();
        String otherEmail  = body.getOrDefault("conversationWith", "").toLowerCase();
        if (otherEmail.isBlank()) return;

        // Mark all unread messages from otherEmail → readerEmail as read
        List<ChatMessage> unread = chatMessageRepository
                .findConversation(readerEmail, otherEmail)
                .stream()
                .filter(m -> m.getRecipientEmail().equals(readerEmail) && m.getReadAt() == null)
                .collect(Collectors.toList());

        if (unread.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now();
        unread.forEach(m -> m.setReadAt(now));
        chatMessageRepository.saveAll(unread);

        // Notify the original sender that their messages were read
        Map<String, Object> receipt = Map.of(
                "type",             "READ_RECEIPT",
                "conversationWith", readerEmail,
                "readAt",           now.toString()
        );
        messagingTemplate.convertAndSendToUser(otherEmail, "/queue/messages", receipt);
    }

    @Operation(summary = "Mark messages as read via REST")
    @PostMapping("/mark-read")
    public ResponseEntity<ApiResponseDTO<Void>> markReadRest(
            @RequestBody Map<String, String> body,
            Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        String readerEmail = principal.getName().toLowerCase();
        String otherEmail  = body.getOrDefault("conversationWith", "").toLowerCase();
        if (otherEmail.isBlank()) return ResponseEntity.badRequest().build();

        // Mark all unread messages from otherEmail → readerEmail as read
        List<ChatMessage> unread = chatMessageRepository
                .findConversation(readerEmail, otherEmail)
                .stream()
                .filter(m -> m.getRecipientEmail() != null && m.getRecipientEmail().equals(readerEmail) && m.getReadAt() == null)
                .collect(Collectors.toList());

        if (!unread.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            unread.forEach(m -> m.setReadAt(now));
            chatMessageRepository.saveAll(unread);

            // Notify the original sender that their messages were read
            Map<String, Object> receipt = Map.of(
                    "type",             "READ_RECEIPT",
                    "conversationWith", readerEmail,
                    "readAt",           now.toString()
            );
            messagingTemplate.convertAndSendToUser(otherEmail, "/queue/messages", receipt);
        }

        return ok("Messages marked as read", null);
    }

    // ── STOMP: presence status ────────────────────────────────
    @MessageMapping("/chat.status")
    public void updateStatus(@Payload Map<String, String> body,
                             SimpMessageHeaderAccessor headerAccessor) {
        Principal principal = headerAccessor.getUser();
        if (principal == null) return;
        String email  = principal.getName().toLowerCase();
        String status = body.getOrDefault("status", "online");
        presenceMap.put(email, status);
        messagingTemplate.convertAndSend("/topic/presence", Map.of("email", email, "status", status));
    }

    @MessageMapping("/chat.connect")
    public void onConnect(SimpMessageHeaderAccessor headerAccessor) {
        Principal principal = headerAccessor.getUser();
        if (principal == null) return;
        String email = principal.getName().toLowerCase();
        presenceMap.put(email, "online");
        messagingTemplate.convertAndSend("/topic/presence", Map.of("email", email, "status", "online"));
    }

    @MessageMapping("/chat.disconnect")
    public void onDisconnect(SimpMessageHeaderAccessor headerAccessor) {
        Principal principal = headerAccessor.getUser();
        if (principal == null) return;
        String email = principal.getName().toLowerCase();
        presenceMap.remove(email);
        messagingTemplate.convertAndSend("/topic/presence", Map.of("email", email, "status", "offline"));
    }

    // ── REST endpoints ────────────────────────────────────────

    @Operation(summary = "Get conversation history")
    @GetMapping("/history")
    public ResponseEntity<ApiResponseDTO<List<ChatMessageDTO.Payload>>> history(
            @Parameter(description = "Email of the other participant", required = true)
            @RequestParam("with") String otherEmail,
            Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        String me = principal.getName().toLowerCase();
        List<ChatMessageDTO.Payload> history = chatMessageRepository
                .findConversation(me, otherEmail.trim().toLowerCase())
                .stream().map(this::toPayload).collect(Collectors.toList());
        return ok("History retrieved", history);
    }

    @Operation(
        summary     = "Send message via REST (fallback when WebSocket is unavailable)",
        description = "Persists the message to DB and delivers via STOMP if recipient is connected. " +
                      "Use this when the WebSocket connection is not yet established."
    )
    @PostMapping("/send")
    public ResponseEntity<ApiResponseDTO<ChatMessageDTO.Payload>> sendRest(
            @RequestBody ChatMessageDTO.SendRequest req,
            Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        String senderEmail    = principal.getName().toLowerCase();
        String recipientEmail = req.getRecipientEmail() != null ? req.getRecipientEmail().trim().toLowerCase() : null;
        
        if (recipientEmail != null && senderEmail.equals(recipientEmail)) return ResponseEntity.badRequest().build();

        User sender = userRepository.findByEmail(senderEmail).orElse(null);
        if (sender == null) return ResponseEntity.status(401).build();

        ChatMessage saved = new ChatMessage(
                senderEmail, sender.getName(),
                sender.getRole() != null ? sender.getRole().name() : "UNKNOWN",
                recipientEmail, req.getContent().trim());
        saved.setDelivered(true);
        chatMessageRepository.save(saved);

        ChatMessageDTO.Payload payload = toPayload(saved);

        // Attempt real-time delivery via STOMP (no-op if recipient not connected)
        try {
            messagingTemplate.convertAndSendToUser(recipientEmail, "/queue/messages", payload);
            messagingTemplate.convertAndSendToUser(senderEmail,    "/queue/messages", payload);
        } catch (Exception ignored) { /* STOMP delivery is best-effort */ }

        return ok("Message sent", payload);
    }

    @Operation(summary = "Get chat contacts")
    @GetMapping("/contacts")
    public ResponseEntity<ApiResponseDTO<List<ChatMessageDTO.ContactDTO>>> contacts(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        String me = principal.getName().toLowerCase();
        List<ChatMessageDTO.ContactDTO> contacts = chatMessageRepository.findContactEmails(me).stream()
                .map(email -> userRepository.findByEmail(email)
                        .map(u -> new ChatMessageDTO.ContactDTO(
                                u.getEmail(), u.getName(),
                                u.getRole() != null ? u.getRole().name() : "",
                                presenceMap.getOrDefault(u.getEmail().toLowerCase(), "offline")))
                        .orElse(new ChatMessageDTO.ContactDTO(email, email, "", "offline")))
                .collect(Collectors.toList());
        return ok("Contacts retrieved", contacts);
    }

    @Operation(summary = "Search users for new conversations")
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<List<ChatMessageDTO.ContactDTO>>> searchUsers(
            @Parameter(description = "Search query (min 2 chars)", required = true)
            @RequestParam("q") String query,
            Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        if (query == null || query.trim().length() < 2) 
            return ok("Query too short", List.of());
        
        String me = principal.getName().toLowerCase();
        String q  = query.trim().toLowerCase();
        List<ChatMessageDTO.ContactDTO> results = userRepository.findAllActive().stream()
                .filter(u -> !u.getEmail().equalsIgnoreCase(me))
                .filter(u -> u.getName().toLowerCase().contains(q) || u.getEmail().toLowerCase().contains(q))
                .map(u -> new ChatMessageDTO.ContactDTO(
                        u.getEmail(), u.getName(),
                        u.getRole() != null ? u.getRole().name() : "",
                        presenceMap.getOrDefault(u.getEmail().toLowerCase(), "offline")))
                .collect(Collectors.toList());
        return ok("Search results", results);
    }

    @Operation(summary = "Get current presence map")
    @GetMapping("/presence")
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> presence() {
        return ok("Presence data", presenceMap);
    }

    @Operation(
        summary     = "Get unread message counts per sender",
        description = "Returns a map of { senderEmail: unreadCount } for all unread messages " +
                      "sent to the authenticated user. Used to restore badge counts on page load."
    )
    @GetMapping("/unread-counts")
    public ResponseEntity<ApiResponseDTO<Map<String, Long>>> unreadCounts(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        String me = principal.getName().toLowerCase();
        Map<String, Long> result = new java.util.HashMap<>();
        chatMessageRepository.findUnreadCountsByRecipient(me)
                .forEach(row -> result.put((String) row[0], (Long) row[1]));
        return ok("Unread counts", result);
    }

    @Operation(summary = "Create a new chat group")
    @PostMapping("/groups")
    public ResponseEntity<ApiResponseDTO<com.sprintflow.entity.ChatGroup>> createGroup(
            @RequestBody Map<String, Object> body,
            Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        String me = principal.getName().toLowerCase();
        String name = (String) body.get("name");
        List<String> memberEmails = (List<String>) body.get("members");
        
        if (name == null || name.isBlank() || memberEmails == null || memberEmails.isEmpty()) 
            return ResponseEntity.badRequest().build();

        com.sprintflow.entity.ChatGroup group = new com.sprintflow.entity.ChatGroup();
        group.setName(name);
        group.setCreatedBy(me);
        
        java.util.Set<User> members = new java.util.HashSet<>();
        userRepository.findByEmail(me).ifPresent(members::add);
        for (String email : memberEmails) {
            userRepository.findByEmail(email.toLowerCase()).ifPresent(members::add);
        }
        group.setMembers(members);
        
        com.sprintflow.entity.ChatGroup saved = chatGroupRepository.save(group);
        return ResponseEntity.status(201).body(ApiResponseDTO.<com.sprintflow.entity.ChatGroup>builder()
                .success(true).message("Group created").data(saved).statusCode(201).build());
    }

    @Operation(summary = "Get groups for the current user")
    @GetMapping("/groups")
    public ResponseEntity<ApiResponseDTO<List<com.sprintflow.entity.ChatGroup>>> getMyGroups(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        List<com.sprintflow.entity.ChatGroup> groups = chatGroupRepository.findByMemberEmail(principal.getName().toLowerCase());
        return ok("Groups retrieved", groups);
    }

    @Operation(summary = "Get group chat history")
    @GetMapping("/groups/{id}/history")
    public ResponseEntity<ApiResponseDTO<List<ChatMessageDTO.Payload>>> groupHistory(
            @PathVariable("id") Long groupId,
            Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        List<ChatMessageDTO.Payload> history = chatMessageRepository.findByRecipientGroupId(groupId)
                .stream().map(this::toPayload).collect(Collectors.toList());
        return ok("Group history retrieved", history);
    }

    private ChatMessageDTO.Payload toPayload(ChatMessage m) {
        return new ChatMessageDTO.Payload(
                m.getId(), m.getSenderEmail(), m.getSenderName(),
                m.getSenderRole(), m.getRecipientEmail(),
                m.getContent(), m.getSentAt(),
                m.isDelivered(), m.getReadAt(), m.getRecipientGroupId());
    }

    private <T> ResponseEntity<ApiResponseDTO<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponseDTO.<T>builder()
                .success(true).message(message).data(data).statusCode(200).build());
    }
}
