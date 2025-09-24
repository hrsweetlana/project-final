package com.javarush.jira.profile.internal.web;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.MatcherFactory;
import com.javarush.jira.common.HasId;
import com.javarush.jira.login.internal.UserRepository;
import com.javarush.jira.profile.ProfileTo;
import com.javarush.jira.profile.internal.ProfileMapper;
import com.javarush.jira.profile.internal.ProfileRepository;
import com.javarush.jira.profile.internal.model.Profile;
import com.javarush.jira.ref.ReferenceService;

import static com.javarush.jira.common.util.JsonUtil.*;
import static com.javarush.jira.profile.internal.web.ProfileTestData.*;
import static com.javarush.jira.login.internal.web.UserTestData.*;
import static com.javarush.jira.profile.internal.web.ProfileRestController.PROFILE_REST_URL;

class ProfileRestControllerTest extends AbstractControllerTest {
	
    @Autowired
    private ReferenceService referenceService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProfileMapper profileMapper;
    
    @Autowired
    private ProfileRepository profileRepository;
    
    @BeforeEach
    void setUp() {
		referenceService.loadReferences();
    }
	
	@Test
	@WithUserDetails(value = USER_MAIL)
	void getUserProfile() throws Exception {
		get(USER_PROFILE_TO, PROFILE_TO_MATCHER, USER_MAIL);
	}
	
	@Test
	@WithUserDetails(value = GUEST_MAIL)
	void getEmptyProfile() throws Exception {
		get(GUEST_PROFILE_EMPTY_TO, PROFILE_TO_MATCHER, GUEST_MAIL);
	}
	
	private <T extends HasId> void get(T expected,  MatcherFactory.Matcher<T> matcher, String mail) throws Exception{
		Long userId = userRepository.getExistedByEmail(mail).getId();
		expected.setId(userId);
		perform(MockMvcRequestBuilders.get(PROFILE_REST_URL))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andExpect(matcher.contentJson(expected));
	}

	@Test
	void getUnAuth() throws Exception {
		perform(MockMvcRequestBuilders.get(PROFILE_REST_URL))
		.andExpect(status().isUnauthorized());
	}
	
	@Test
	@WithAnonymousUser
	void getAuthWithAnonymousUser() throws Exception {
		perform(MockMvcRequestBuilders.get(PROFILE_REST_URL))
		.andExpect(status().isUnauthorized());
	}
    
    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateUserProfile1() throws Exception{
    	updateProfile(USER_MAIL, USER_ID);
    }
    
    @Test
    @WithUserDetails(value = GUEST_MAIL)
    void updateEmptyProfile1() throws Exception{
    	updateProfile(GUEST_MAIL, GUEST_ID);
    }
    
    private <T> void put(T expected, ResultMatcher matcher) throws Exception{
    	
    	perform(MockMvcRequestBuilders.put(PROFILE_REST_URL)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(writeValue(expected)))
    			.andDo(print())
    			.andExpect(matcher);
    }
    
    private void updateProfile(String userMail, Long user_Id) throws Exception{
    	Long userId = userRepository.getExistedByEmail(userMail).getId();
    	ProfileTo profileToUpdated = getUpdatedTo();
    	
        put(profileToUpdated, status().isNoContent());
    	
    	profileToUpdated.setId(userId);
    	
    	ProfileTo profileActual = profileMapper.toTo(profileRepository.getExisted(userId));
    	ProfileTo profileExpected = profileMapper.toTo(getUpdated(user_Id));
    	
    	Profile profile = new Profile();
    	Profile profileUpdatedExpected = profileMapper.updateFromTo(profile, profileToUpdated);
    	Profile profileUpdatedActual = profileMapper.updateFromTo(profile, profileExpected);
    	
     	PROFILE_TO_MATCHER.assertMatch(profileActual, profileExpected);
    	PROFILE_MATCHER.assertMatch(profileUpdatedExpected, profileUpdatedActual);
    }
    
    @Test
    @WithUserDetails(USER_MAIL)
    void updateWhenInvalidNotifications() throws Exception{
        put(getWithInvalidNotification(), status().isUnprocessableEntity());
    }
    
    @Test
    @WithUserDetails(USER_MAIL)
    void updateWhenInvalidContactValue() throws Exception{
        put(getWithInvalidContactValue(), status().isUnprocessableEntity());
    }
    
    @Test
    @WithUserDetails(USER_MAIL)
    void updateWhenInvalidContactCode() throws Exception{
        put(getWithInvalidContactCode(), status().isUnprocessableEntity());
    }
    
    @Test
    @WithUserDetails(USER_MAIL)
    void updateWhenEmptyNotificationsAndContactValue() throws Exception{
        put(getInvalidTo(), status().isUnprocessableEntity());
    }
    
    @Test
    @WithUserDetails(USER_MAIL)
    void updateWhenWrongNotificationsAndEmptyContacts() throws Exception{
        put(getWithUnknownNotificationTo(), status().isUnprocessableEntity());
    }
    
    @Test
    @WithUserDetails(USER_MAIL)
    void updateWhenEmptyNotificationAndWrongContactCode() throws Exception{
        put(getWithUnknownContactTo(), status().isUnprocessableEntity());
    } 
    
    @Test
    @WithUserDetails(USER_MAIL)
    void updateWhenContactHtmlUnsafeTo() throws Exception{
        put(getWithContactHtmlUnsafeTo(), status().isUnprocessableEntity());
    }
}