package com.javarush.jira.ref;

import com.javarush.jira.bugtracking.ObjectType;
import com.javarush.jira.common.internal.config.AppConfig;
import com.javarush.jira.ref.internal.ReferenceMapper;
import com.javarush.jira.ref.internal.ReferenceRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.javarush.jira.common.util.Util.getExisted;

@Service
@RequiredArgsConstructor
@Slf4j
//@DependsOn("liquibase")
public class ReferenceService {
    static Map<RefType, Map<String, RefTo>> refSelect;
    @Lazy
    @Autowired
    private ReferenceRepository repository;
    private final ReferenceMapper mapper;
    private final AppConfig appConfig;

    public static Map<String, RefTo> getRefs(RefType refType) {
        log.debug("get by type {}", refType);
        return getExisted(refSelect, refType);
    }

    public static Map<String, RefTo> getRefsByTypeStartWithObjectType(RefType refType, @NonNull ObjectType objectType) {
        log.debug("get by type {} start with objectType {}", refType, objectType);
        String type = objectType.name().toLowerCase();
        return getRefs(refType).entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(type))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static RefTo getRefTo(RefType refType, String code) {
        log.debug("get by type {} and code {}", refType, code);
        return getExisted(getRefs(refType), code);
    }

    public static Map<String, RefTo> filterEnabled(Map<String, RefTo> unfilteredRefs) {
        log.debug("filterEnabled");
        return unfilteredRefs.entrySet().stream()
                .filter(ref -> ref.getValue().isEnabled())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    //@PostConstruct
    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
//		if (env.acceptsProfiles(Profiles.of("prod"))) {
//    	       loadReferences();
//    	   }
    	if(appConfig.isProd() || appConfig.isDev()) {
    		loadReferences();
    	}
    }
    
    public void loadReferences() {
    	 log.info("init loading");
         List<RefTo> references = Optional.ofNullable(mapper.toToList(repository.findAllByOrderByIdAsc()))
                 .orElse(Collections.emptyList());
         refSelect = references.stream()
                 .collect(Collectors.groupingBy(RefTo::getRefType,
                         Collectors.collectingAndThen(Collectors.toMap(RefTo::getCode, Function.identity(), (ref1, ref2) -> ref1, LinkedHashMap::new), Collections::unmodifiableMap)));
         //log.info(refSelect.toString());
    }

    public void updateRefs(RefType type) {
        log.debug("update by type {}", type);
        List<RefTo> refTos = Optional.ofNullable(mapper.toToList(repository.getByType(type)))
                .orElse(Collections.emptyList());
        Map<String, RefTo> refToMap = refTos.stream()
                .collect(Collectors.toMap(RefTo::getCode, Function.identity()));
        refSelect.put(type, refToMap);
    }
}
