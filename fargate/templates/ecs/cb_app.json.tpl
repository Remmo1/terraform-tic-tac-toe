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
      { "name": "POSTGRES_USER", "value": "uzytkownik" },
      { "name": "POSTGRES_PASSWORD", "value": "P@ssw0rd!"}
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
      { "name": "SPRING_DATASOURCE_USERNAME", "value": "uzytkownik" },
      { "name": "SPRING_DATASOURCE_PASSWORD", "value": "P@ssw0rd!"}
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
      { "name": "REACT_APP_USER_POOL_ID", "value": "us-east-1_9LgcvyFtD" },
      { "name": "REACT_APP_CLIENT_ID", "value": "243u68h4ne12orqgmh5a8ombvu" },
      { "name": "DANGEROUSLY_DISABLE_HOST_CHECK", "value": "true" }
    ]
  }
]
