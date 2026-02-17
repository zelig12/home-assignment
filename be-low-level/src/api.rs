use axum::{
    extract::State,
    http::{HeaderMap, StatusCode},
    response::Json,
};
use axum::response::IntoResponse;
use serde::{Deserialize, Serialize};
use std::sync::Arc;

use crate::game_service::GameService;
use crate::login_service::LoginService;

const BEARER_PREFIX: &str = "Bearer ";

#[derive(Deserialize)]
pub struct LoginRequest {
    pub email: String,
    pub password: String,
}

#[derive(Serialize)]
pub struct LoginResponse {
    pub token: String,
}

#[derive(Serialize)]
pub struct ErrorResponse {
    pub error: String,
}

#[derive(Serialize)]
pub struct TryLuckResponse {
    pub win: bool,
}

pub struct AppState {
    pub login_service: Arc<LoginService>,
    pub game_service: Arc<GameService>,
}

pub async fn method_not_allowed() -> impl IntoResponse {
    (
        StatusCode::METHOD_NOT_ALLOWED,
        Json(ErrorResponse {
            error: "Method not allowed. Use POST.".to_string(),
        }),
    )
}

fn extract_bearer_token(headers: &HeaderMap) -> Option<String> {
    let auth_header = headers.get("authorization")?.to_str().ok()?;
    if !auth_header.starts_with(BEARER_PREFIX) {
        return None;
    }
    Some(auth_header[BEARER_PREFIX.len()..].trim().to_string())
}

pub async fn login(
    State(state): State<Arc<AppState>>,
    Json(request): Json<LoginRequest>,
) -> Result<Json<LoginResponse>, (StatusCode, Json<ErrorResponse>)> {
    match state.login_service.login(&request.email, &request.password) {
        Some(token) => Ok(Json(LoginResponse { token })),
        None => Err((
            StatusCode::UNAUTHORIZED,
            Json(ErrorResponse {
                error: "Invalid email or password".to_string(),
            }),
        )),
    }
}

pub async fn logout(
    State(state): State<Arc<AppState>>,
    headers: HeaderMap,
) -> Result<&'static str, (StatusCode, Json<ErrorResponse>)> {
    let token = extract_bearer_token(&headers)
        .ok_or_else(|| {
            (
                StatusCode::UNAUTHORIZED,
                Json(ErrorResponse {
                    error: "Unauthorized".to_string(),
                }),
            )
        })?;

    if !state.login_service.invalidate_token(&token) {
        return Err((
            StatusCode::UNAUTHORIZED,
            Json(ErrorResponse {
                error: "Unauthorized".to_string(),
            }),
        ));
    }

    Ok("OK")
}

pub async fn try_luck(
    State(state): State<Arc<AppState>>,
    headers: HeaderMap,
) -> Result<Json<TryLuckResponse>, (StatusCode, Json<ErrorResponse>)> {
    let token = extract_bearer_token(&headers)
        .ok_or_else(|| {
            (
                StatusCode::UNAUTHORIZED,
                Json(ErrorResponse {
                    error: "Unauthorized".to_string(),
                }),
            )
        })?;

    if !state.login_service.is_valid_token(&token) {
        return Err((
            StatusCode::UNAUTHORIZED,
            Json(ErrorResponse {
                error: "Unauthorized".to_string(),
            }),
        ));
    }

    let win = state.game_service.try_luck();
    Ok(Json(TryLuckResponse { win }))
}
