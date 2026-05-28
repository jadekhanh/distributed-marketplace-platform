# distributed-video-streaming-platform
A backend system where users can securely upload videos, the system stores the raw video in cloud object storage, processes the video asynchronously through Kafka workers, saves video metadata in MySQL, caches active playback/session data in Redis, and sends real-time upload/transcoding status updates through WebSockets.
