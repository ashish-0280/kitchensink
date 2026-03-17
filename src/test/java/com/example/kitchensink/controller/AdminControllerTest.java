package com.example.kitchensink.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminController Tests")
class AdminControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private AdminController adminController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
    }

    private MemberRequestDto validRequest() {
        MemberRequestDto dto = new MemberRequestDto();
        dto.setName("John Doe");
        dto.setEmail("john@example.com");
        dto.setPhone("1234567890");
        dto.setPassword("Password@123");
        return dto;
    }

    private MemberResponseDto sampleResponse(String id) {
        MemberResponseDto dto = new MemberResponseDto();
        dto.setId(id);
        dto.setName("John Doe");
        dto.setEmail("john@example.com");
        dto.setPhone("1234567890");
        return dto;
    }

    @Nested
    @DisplayName("POST /admin/members/create")
    class CreateMemberTests {

        @Test
        @DisplayName("Should return 200 OK with MemberResponseDto when request is valid")
        void create_WithValidRequest_ReturnsOk() throws Exception {
            MemberRequestDto request = validRequest();
            MemberResponseDto response = sampleResponse("abc123");

            when(memberService.create(any(MemberRequestDto.class))).thenReturn(response);

            mockMvc.perform(post("/admin/members/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("abc123"))
                    .andExpect(jsonPath("$.email").value("john@example.com"));

            verify(memberService, times(1)).create(any(MemberRequestDto.class));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when request body is invalid")
        void create_WithInvalidRequest_ReturnsBadRequest() throws Exception {
            // empty body — triggers @Valid failure
            mockMvc.perform(post("/admin/members/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verify(memberService, never()).create(any(MemberRequestDto.class));
        }
    }


    @Nested
    @DisplayName("GET /admin/members/edit/{id}")
    class EditMemberTests {

        @Test
        @DisplayName("Should return edit-profile view with model attributes when member exists")
        void editMember_WithValidId_ReturnsEditProfileView() throws Exception {
            MemberResponseDto member = sampleResponse("abc123");
            when(memberService.getById("abc123")).thenReturn(member);

            mockMvc.perform(get("/admin/members/edit/abc123"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("edit-profile"))
                    .andExpect(model().attribute("user", member))
                    .andExpect(model().attribute("isAdminView", true));

            verify(memberService, times(1)).getById("abc123");
        }
    }


    @Nested
    @DisplayName("GET /admin/members")
    class GetAllMembersTests {

        @Test
        @DisplayName("Should return AdminDashboard view with paginated members")
        void getAllMembers_WithDefaultParams_ReturnsAdminDashboard() throws Exception {
            List<MemberResponseDto> memberList = List.of(sampleResponse("1"), sampleResponse("2"));
            Page<MemberResponseDto> page = new PageImpl<>(memberList, PageRequest.of(0, 5), 2);

            when(memberService.searchMembers(isNull(), any())).thenReturn(page);

            mockMvc.perform(get("/admin/members"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("AdminDashboard"))
                    .andExpect(model().attribute("allMembers", memberList))
                    .andExpect(model().attribute("currentPage", 0))
                    .andExpect(model().attribute("totalPages", 1))
                    .andExpect(model().attribute("totalMembers", 2L));

            verify(memberService, times(1)).searchMembers(isNull(), any());
        }

        @Test
        @DisplayName("Should pass keyword to service when keyword param is provided")
        void getAllMembers_WithKeyword_PassesKeywordToService() throws Exception {
            List<MemberResponseDto> memberList = List.of(sampleResponse("1"));
            Page<MemberResponseDto> page = new PageImpl<>(memberList, PageRequest.of(0, 5), 1);

            when(memberService.searchMembers(eq("john"), any())).thenReturn(page);

            mockMvc.perform(get("/admin/members")
                            .param("keyword", "john"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("AdminDashboard"))
                    .andExpect(model().attribute("keyword", "john"));

            verify(memberService, times(1)).searchMembers(eq("john"), any());
        }

        @Test
        @DisplayName("Should return correct page when page and size params are provided")
        void getAllMembers_WithPageParams_ReturnsCorrectPage() throws Exception {
            Page<MemberResponseDto> page = new PageImpl<>(List.of(), PageRequest.of(2, 10), 0);

            when(memberService.searchMembers(isNull(), any())).thenReturn(page);

            mockMvc.perform(get("/admin/members")
                            .param("page", "2")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("currentPage", 2));
        }
    }


    @Nested
    @DisplayName("GET /admin/members/search")
    class SearchMembersTests {

        @Test
        @DisplayName("Should return paginated JSON response when searching without keyword")
        void searchMembers_WithoutKeyword_ReturnsPaginatedJson() throws Exception {
            List<MemberResponseDto> memberList = List.of(sampleResponse("1"));
            Page<MemberResponseDto> page = new PageImpl<>(memberList, PageRequest.of(0, 5), 1);

            when(memberService.searchMembers(isNull(), any())).thenReturn(page);

            mockMvc.perform(get("/admin/members/search"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value("1"));

            verify(memberService, times(1)).searchMembers(isNull(), any());
        }

        @Test
        @DisplayName("Should return filtered results when keyword is provided")
        void searchMembers_WithKeyword_ReturnsFilteredResults() throws Exception {
            List<MemberResponseDto> memberList = List.of(sampleResponse("2"));
            Page<MemberResponseDto> page = new PageImpl<>(memberList, PageRequest.of(0, 5), 1);

            when(memberService.searchMembers(eq("john"), any())).thenReturn(page);

            mockMvc.perform(get("/admin/members/search")
                            .param("keyword", "john"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value("2"));

            verify(memberService, times(1)).searchMembers(eq("john"), any());
        }

        @Test
        @DisplayName("Should return empty page when no members match keyword")
        void searchMembers_WithNonMatchingKeyword_ReturnsEmptyPage() throws Exception {
            Page<MemberResponseDto> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 5), 0);

            when(memberService.searchMembers(eq("unknown"), any())).thenReturn(emptyPage);

            mockMvc.perform(get("/admin/members/search")
                            .param("keyword", "unknown"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }
    }


    @Nested
    @DisplayName("GET /admin/members/profile/{id}")
    class ViewMemberProfileTests {

        @Test
        @DisplayName("Should return profile view with member data when member exists")
        void viewMemberProfile_WithValidId_ReturnsProfileView() throws Exception {
            MemberResponseDto member = sampleResponse("abc123");
            when(memberService.getById("abc123")).thenReturn(member);

            mockMvc.perform(get("/admin/members/profile/abc123"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("profile"))
                    .andExpect(model().attribute("user", member))
                    .andExpect(model().attribute("isAdminView", true));

            verify(memberService, times(1)).getById("abc123");
        }
    }


    @Nested
    @DisplayName("DELETE /admin/members/{id}")
    class DeleteMemberTests {

        @Test
        @DisplayName("Should return 200 OK with 'Deleted' message when member is successfully deleted")
        void deleteMember_WithValidId_ReturnsDeletedMessage() throws Exception {
            doNothing().when(memberService).delete("abc123");

            mockMvc.perform(delete("/admin/members/abc123"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Deleted"));

            verify(memberService, times(1)).delete("abc123");
        }

        @Test
        @DisplayName("Should call delete service exactly once per request")
        void deleteMember_CallsServiceOnce() throws Exception {
            doNothing().when(memberService).delete(anyString());

            mockMvc.perform(delete("/admin/members/xyz999"))
                    .andExpect(status().isOk());

            verify(memberService, times(1)).delete("xyz999");
        }
    }
}