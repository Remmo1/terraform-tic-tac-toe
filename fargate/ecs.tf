resource "aws_ecs_cluster" "main" {
    name = "cb-cluster"
}

data "template_file" "cb_app" {
    template = file("./templates/ecs/cb_app.json.tpl")

    vars = {
        fargate_cpu    = var.fargate_cpu
        fargate_memory = var.fargate_memory
        aws_region     = var.aws_region
        
        app_cpu         = var.app_cpu
        app_memory      = var.app_memory

        db_image       = var.db_image
        db_port        = var.db_port
        backend_image  = var.backend_image
        backend_port   = var.backend_port
        frontend_image = var.frontend_image
        frontend_port  = var.frontend_port

        db_user = jsondecode(data.aws_secretsmanager_secret_version.secret_credentials.secret_string)["db_user"]
        db_password = jsondecode(data.aws_secretsmanager_secret_version.secret_credentials.secret_string)["db_password"]
        front_user_pool_id = jsondecode(data.aws_secretsmanager_secret_version.secret_credentials.secret_string)["front_user_pool_id"]
        front_client_id = jsondecode(data.aws_secretsmanager_secret_version.secret_credentials.secret_string)["front_client_id"]
    }
}

resource "aws_ecs_task_definition" "app" {
    family                   = "cb-app-task"
    network_mode             = "awsvpc"
    requires_compatibilities = ["FARGATE"]
    cpu                      = var.fargate_cpu
    memory                   = var.fargate_memory
    container_definitions    = data.template_file.cb_app.rendered
    execution_role_arn       = "${data.aws_iam_role.ecs_task_execution_role.arn}"
}

resource "aws_ecs_service" "main" {
    name            = "cb-service"
    cluster         = aws_ecs_cluster.main.id
    task_definition = aws_ecs_task_definition.app.arn
    desired_count   = var.app_count
    launch_type     = "FARGATE"

    network_configuration {
        security_groups  = [aws_security_group.ecs_tasks.id]
        subnets          = aws_subnet.private.*.id
        assign_public_ip = true
    }

    load_balancer {
        target_group_arn = aws_alb_target_group.app.id
        container_name   = "frontend"
        container_port   = var.frontend_port
    }

    depends_on = [aws_alb_listener.front_end]
}
