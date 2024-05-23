# alb.tf

resource "aws_alb" "main" {
    name        = "cb-load-balancer"
    subnets         = aws_subnet.public.*.id
    security_groups = [aws_security_group.lb.id]
}

resource "aws_alb_target_group" "app" {
    name        = "cb-target-group"
    port        = 80
    protocol    = "HTTP"
    vpc_id      = aws_vpc.main.id
    target_type = "ip"

    stickiness {
      type = "app_cookie"
      enabled = true
      cookie_name = "Cookie"
      cookie_duration = 600
    }

    health_check {
        healthy_threshold   = "2"
        interval            = "30"
        protocol            = "HTTP"
        matcher             = "200"
        timeout             = "2"
        path                = var.health_check_path
        unhealthy_threshold = "2"
    }
}

# Redirect all traffic from the ALB to the target group
resource "aws_alb_listener" "front_end" {
  load_balancer_arn = aws_alb.main.id
  port              = var.frontend_port
  protocol          = "HTTP"

  default_action {
    target_group_arn = aws_alb_target_group.app.id
    type             = "forward"
  }
}
