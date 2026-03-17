package com.example.kitchensink.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.kitchensink.dto.MemberRequestDto;
import com.example.kitchensink.dto.MemberResponseDto;
import com.example.kitchensink.service.MemberService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberController Tests")
class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private MemberResponseDto sampleResponse(String id, String email) {
        MemberResponseDto dto = new MemberResponseDto();
        dto.setId(id);
        dto.setEmail(email);
        dto.setName("John Doe");
        dto.setPhone("1234567890");
        return dto;
    }

    private void setAuthentication(String email) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(email, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


    @Nested
    @DisplayName("GET /member/profile")
    class GetProfileTests {

        @Test
        @DisplayName("Should return profile view using email from SecurityContext when authentication is set")
        void getProfile_WithSecurityContextAuthentication_ReturnsProfileView() throws Exception {
            String email = "john@example.com";
            setAuthentication(email);

            MemberResponseDto member = sampleResponse("abc123", email);
            when(memberService.getByEmail(email)).thenReturn(member);

            mockMvc.perform(get("/member/profile"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("profile"))
                    .andExpect(model().attribute("user", member))
                    .andExpect(model().attribute("isAdminView", false));

            verify(memberService, times(1)).getByEmail(email);
        }

        @Test
        @DisplayName("Should return profile view using email from Principal when SecurityContext is empty")
        void getProfile_WithPrincipalOnly_ReturnsProfileView() throws Exception {
            SecurityContextHolder.clearContext();

            String email = "jane@example.com";
            MemberResponseDto member = sampleResponse("def456", email);
            when(memberService.getByEmail(email)).thenReturn(member);

            Principal mockPrincipal = () -> email;

            mockMvc.perform(get("/member/profile").principal(mockPrincipal))
                    .andExpect(status().isOk())
                    .andExpect(view().name("profile"))
                    .andExpect(model().attribute("user", member))
                    .andExpect(model().attribute("isAdminView", false));

            verify(memberService, times(1)).getByEmail(email);
        }

        @Test
        @DisplayName("Should set isAdminView to false in model")
        void getProfile_SetsIsAdminViewFalse() throws Exception {
            String email = "john@example.com";
            setAuthentication(email);

            when(memberService.getByEmail(email)).thenReturn(sampleResponse("abc123", email));

            mockMvc.perform(get("/member/profile"))
                    .andExpect(model().attribute("isAdminView", false));
        }

        @Test
        @DisplayName("Should call memberService.getByEmail exactly once")
        void getProfile_CallsServiceOnce() throws Exception {
            String email = "john@example.com";
            setAuthentication(email);

            when(memberService.getByEmail(email)).thenReturn(sampleResponse("abc123", email));

            mockMvc.perform(get("/member/profile"))
                    .andExpect(status().isOk());

            verify(memberService, times(1)).getByEmail(email);
        }
    }

    @Nested
    @DisplayName("GET /member/{id}")
    class GetMemberTests {

        @Test
        @DisplayName("Should return 200 OK with MemberResponseDto when member exists")
        void getMember_WithValidId_ReturnsOk() throws Exception {
            MemberResponseDto member = sampleResponse("abc123", "john@example.com");
            when(memberService.getById("abc123")).thenReturn(member);

            mockMvc.perform(get("/member/abc123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("abc123"))
                    .andExpect(jsonPath("$.email").value("john@example.com"));

            verify(memberService, times(1)).getById("abc123");
        }

        @Test
        @DisplayName("Should call memberService.getById exactly once per request")
        void getMember_CallsServiceOnce() throws Exception {
            when(memberService.getById(anyString())).thenReturn(sampleResponse("xyz", "a@b.com"));

            mockMvc.perform(get("/member/xyz"))
                    .andExpect(status().isOk());

            verify(memberService, times(1)).getById("xyz");
        }
    }


    @Nested
    @DisplayName("GET /member/edit/{id}")
    class EditProfileTests {

        @Test
        @DisplayName("Should return edit-profile view with member data and isAdminView false")
        void editProfile_WithValidId_ReturnsEditProfileView() throws Exception {
            MemberResponseDto member = sampleResponse("abc123", "john@example.com");
            when(memberService.getById("abc123")).thenReturn(member);

            mockMvc.perform(get("/member/edit/abc123"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("edit-profile"))
                    .andExpect(model().attribute("user", member))
                    .andExpect(model().attribute("isAdminView", false));

            verify(memberService, times(1)).getById("abc123");
        }

        @Test
        @DisplayName("Should set isAdminView to false in model")
        void editProfile_SetsIsAdminViewFalse() throws Exception {
            when(memberService.getById("abc123")).thenReturn(sampleResponse("abc123", "john@example.com"));

            mockMvc.perform(get("/member/edit/abc123"))
                    .andExpect(model().attribute("isAdminView", false));
        }

        @Test
        @DisplayName("Should call memberService.getById exactly once")
        void editProfile_CallsServiceOnce() throws Exception {
            when(memberService.getById(anyString())).thenReturn(sampleResponse("abc123", "john@example.com"));

            mockMvc.perform(get("/member/edit/abc123"))
                    .andExpect(status().isOk());

            verify(memberService, times(1)).getById("abc123");
        }
    }

    // ─── POST /member/update/{id} ─────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /member/update/{id}")
    class UpdateProfileTests {

        @Test
        @DisplayName("Should redirect to /member/profile after successful update")
        void updateProfile_WithValidData_RedirectsToProfile() throws Exception {
            doNothing().when(memberService).update(eq("abc123"), any(MemberRequestDto.class));

            mockMvc.perform(post("/member/update/abc123")
                            .param("name", "John Doe")
                            .param("email", "john@example.com")
                            .param("phoneNumber", "1234567890"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/member/profile"));

            verify(memberService, times(1)).update(eq("abc123"), any(MemberRequestDto.class));
        }

        @Test
        @DisplayName("Should call memberService.update exactly once per request")
        void updateProfile_CallsServiceOnce() throws Exception {
            doNothing().when(memberService).update(anyString(), any(MemberRequestDto.class));

            mockMvc.perform(post("/member/update/abc123")
                            .param("name", "Jane Doe"))
                    .andExpect(status().is3xxRedirection());

            verify(memberService, times(1)).update(eq("abc123"), any(MemberRequestDto.class));
        }

        @Test
        @DisplayName("Should redirect to /member/profile even when form fields are empty")
        void updateProfile_WithEmptyParams_StillRedirects() throws Exception {
            doNothing().when(memberService).update(anyString(), any(MemberRequestDto.class));

            mockMvc.perform(post("/member/update/abc123"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/member/profile"));
        }
    }
}