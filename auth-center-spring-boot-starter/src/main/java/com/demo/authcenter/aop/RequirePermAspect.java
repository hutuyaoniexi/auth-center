package com.demo.authcenter.aop;

import com.demo.authcenter.annotation.RequirePerm;
import com.demo.authcenter.permission.Permission;
import com.demo.authcenter.permission.PermissionChecker;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

@Aspect
public class RequirePermAspect {

    private final PermissionChecker checker;

    public RequirePermAspect(PermissionChecker checker) {
        this.checker = checker;
    }

    @Before("@annotation(requirePerm)")
    public void check(RequirePerm requirePerm) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        List<String> requiredPerms = resolvePerms(requirePerm);

        boolean passed = requirePerm.requireAll()
                ? requiredPerms.stream().allMatch(p -> checker.hasPerm(auth, p))
                : requiredPerms.stream().anyMatch(p -> checker.hasPerm(auth, p));

        if (!passed) {
            throw new AccessDeniedException("Forbidden");
        }
    }

    private List<String> resolvePerms(RequirePerm requirePerm) {
        Class<? extends Enum<?>> enumClass = requirePerm.perm();
        String[] actions = requirePerm.actions();

        if (actions == null || actions.length == 0) {
            throw new IllegalArgumentException("@RequirePerm.actions() must not be empty");
        }

        List<String> perms = new ArrayList<>(actions.length);
        for (String action : actions) {
            if (action == null || action.isBlank()) {
                throw new IllegalArgumentException("@RequirePerm.actions() contains blank action");
            }
            perms.add(resolveOne(enumClass, action.trim()));
        }
        return perms;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private String resolveOne(Class<? extends Enum<?>> enumClass, String name) {
        final Enum e;
        try {
            e = Enum.valueOf((Class) enumClass, name);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Unknown permission action '" + name + "' for enum " + enumClass.getName(), ex
            );
        }

        if (!(e instanceof Permission p)) {
            throw new IllegalArgumentException(
                    "Enum " + enumClass.getName() + " must implement " + Permission.class.getName()
            );
        }
        return p.value();
    }
}
