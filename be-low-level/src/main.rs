mod api;
mod game_service;
mod login_service;

use axum::{
    routing::{get, post},
    Router,
};
use std::sync::Arc;
use tower_http::cors::CorsLayer;

use api::AppState;
use game_service::GameService;
use login_service::LoginService;

#[tokio::main]
async fn main() {
    // Initialize services
    let login_service = Arc::new(LoginService::new());
    let game_service = Arc::new(GameService::new());

    let app_state = Arc::new(AppState {
        login_service,
        game_service,
    });

    // Build the router
    let app = Router::new()
        .route("/api/login", post(api::login).get(api::method_not_allowed))
        .route("/api/logout", post(api::logout).get(api::method_not_allowed))
        .route("/api/try_luck", post(api::try_luck).get(api::method_not_allowed))
        .layer(CorsLayer::permissive())
        .with_state(app_state);

    // Start the server (port 4000 for docker-compose / nginx, override with PORT env for local)
    let port = std::env::var("PORT").unwrap_or_else(|_| "4000".to_string());
    let addr = format!("0.0.0.0:{}", port);
    let listener = tokio::net::TcpListener::bind(&addr)
        .await
        .expect("Failed to bind");
    println!("Server running on http://{}", addr);

    axum::serve(listener, app)
        .await
        .expect("Server failed to start");
}
