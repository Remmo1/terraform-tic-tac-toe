# General
variable region {
  type        = string
  default     = "us-east-1"
  description = "Default region for provider"
}

# Ec2
variable ami {
    type        = string
    default     = "ami-0c101f26f147fa7fd"
    description = "Amazon Linux image name"
}

variable instance_type {
    type        = string
    default     = "t2.medium"
    description = "Linux machine size"
}

variable key_private {
    type        = string
    description = "Private key name"
}

variable key_public {
    type        = string
    description = "Private key name"
}
