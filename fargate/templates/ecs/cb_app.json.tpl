[
  {
    "name": "postgres_db",
    "image": "${db_image}",
    "cpu": ${app_cpu},
    "memory": ${app_memory},
    "networkMode": "awsvpc",
    "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/cb-app",
          "awslogs-region": "${aws_region}",
          "awslogs-stream-prefix": "ecs"
        }
    },
    "portMappings": [
      {
        "containerPort": ${db_port},
        "hostPort": ${db_port}
      }
    ],
    "environment": [
      { "name": "POSTGRES_DB", "value": "games" },
      { "name": "POSTGRES_USER", "value": "${db_user}" },
      { "name": "POSTGRES_PASSWORD", "value": "${db_password}" }
    ]
  },
  {
    "name": "backend",
    "image": "${backend_image}",
    "cpu": ${app_cpu},
    "memory": ${app_memory},
    "networkMode": "awsvpc",
    "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/cb-app",
          "awslogs-region": "${aws_region}",
          "awslogs-stream-prefix": "ecs"
        }
    },
    "portMappings": [
      {
        "containerPort": ${backend_port},
        "hostPort": ${backend_port}
      }
    ],
    "environment": [
      { "name": "SOCKET-SERVER_PORT", "value": "8080" },
      { "name": "SPRING_DATASOURCE_URL", "value": "jdbc:postgresql://localhost:5432/games" },
      { "name": "SPRING_DATASOURCE_USERNAME", "value": "${db_user}" },
      { "name": "SPRING_DATASOURCE_PASSWORD", "value": "${db_password}" }
    ]
  },
  {
    "name": "frontend",
    "image": "${frontend_image}",
    "cpu": ${app_cpu},
    "memory": ${app_memory},
    "networkMode": "awsvpc",
    "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/cb-app",
          "awslogs-region": "${aws_region}",
          "awslogs-stream-prefix": "ecs"
        }
    },
    "portMappings": [
      {
        "containerPort": ${frontend_port},
        "hostPort": ${frontend_port}
      }
    ],
    "environment": [
      { "name": "REACT_APP_BACKEND_LINK", "value": "/" },
      { "name": "REACT_APP_USER_POOL_ID", "value": "${front_user_pool_id}" },
      { "name": "REACT_APP_CLIENT_ID", "value": "${front_client_id}" },
      { "name": "DANGEROUSLY_DISABLE_HOST_CHECK", "value": "true" }
    ]
  }
]
