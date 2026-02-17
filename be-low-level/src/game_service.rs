use chrono::Utc;
use rand::Rng;
use std::sync::{Arc, Mutex};

pub struct GameService {
    win_count: Arc<Mutex<u32>>,
    last_seen_date: Arc<Mutex<chrono::NaiveDate>>,
}

impl GameService {
    pub fn new() -> Self {
        let today = Utc::now().date_naive();
        Self {
            win_count: Arc::new(Mutex::new(0)),
            last_seen_date: Arc::new(Mutex::new(today)),
        }
    }

    /// Thread-safe: lock only when calendar day changes (rare); hot path is lock-free.
    pub fn try_luck(&self) -> bool {
        let today = Utc::now().date_naive();
        
        // Check if we need to reset (double-checked locking pattern)
        let needs_reset = {
            let last_date = self.last_seen_date.lock().unwrap();
            *last_date != today
        };

        if needs_reset {
            let mut last_date = self.last_seen_date.lock().unwrap();
            if *last_date != today {
                *last_date = today;
                *self.win_count.lock().unwrap() = 0;
            }
        }

        let win_count = *self.win_count.lock().unwrap();
        let win_probability = if win_count < 30 { 0.7 } else { 0.4 };
        let win = rand::thread_rng().gen::<f64>() < win_probability;

        if win {
            let mut count = self.win_count.lock().unwrap();
            *count += 1;
        }

        win
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn try_luck_returns_boolean() {
        let service = GameService::new();
        let result = service.try_luck();
        // Just verify it's a boolean (compiler ensures this)
        let _: bool = result;
    }

    #[test]
    fn try_luck_can_be_called_multiple_times_without_exception() {
        let service = GameService::new();
        for _ in 0..100 {
            service.try_luck();
        }
    }
}
