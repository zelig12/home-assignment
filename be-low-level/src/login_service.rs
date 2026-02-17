use dashmap::DashSet;
use once_cell::sync::Lazy;
use regex::Regex;
use std::sync::Arc;
use uuid::Uuid;

pub struct LoginService {
    valid_tokens: Arc<DashSet<String>>,
}

impl LoginService {
    pub const PASSWORD: &'static str = "r2isthebest";

    pub fn new() -> Self {
        Self {
            valid_tokens: Arc::new(DashSet::new()),
        }
    }

    pub fn login(&self, email: &str, password: &str) -> Option<String> {
        if !self.is_valid_email(email) || password != Self::PASSWORD {
            return None;
        }
        let token = Uuid::new_v4().to_string();
        self.valid_tokens.insert(token.clone());
        Some(token)
    }

    pub fn invalidate_token(&self, token: &str) -> bool {
        token.is_empty() == false && self.valid_tokens.remove(token).is_some()
    }

    pub fn is_valid_token(&self, token: &str) -> bool {
        !token.is_empty() && self.valid_tokens.contains(token)
    }

    fn is_valid_email(&self, email: &str) -> bool {
        if email.is_empty() {
            return false;
        }
        // Regex pattern matching Java: ^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$
        static EMAIL_REGEX: Lazy<Regex> = Lazy::new(|| {
            Regex::new(r"^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").unwrap()
        });
        EMAIL_REGEX.is_match(email)
    }
}

impl Default for LoginService {
    fn default() -> Self {
        Self::new()
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn returns_token_when_email_and_password_are_valid() {
        let service = LoginService::new();
        let result = service.login("user@example.com", LoginService::PASSWORD);

        assert!(result.is_some());
        assert!(!result.as_ref().unwrap().is_empty());
        assert!(service.is_valid_token(result.as_ref().unwrap()));
    }

    #[test]
    fn returns_none_when_credentials_are_invalid() {
        let service = LoginService::new();
        assert!(service.login("user@example.com", "wrong").is_none());
        assert!(service.login("", LoginService::PASSWORD).is_none());
    }

    #[test]
    fn invalidate_token_removes_token_so_it_is_no_longer_valid() {
        let service = LoginService::new();
        let token = service.login("user@example.com", LoginService::PASSWORD).unwrap();
        let removed = service.invalidate_token(&token);

        assert!(removed);
        assert!(!service.is_valid_token(&token));
    }
}
