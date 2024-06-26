terraform {
    required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.47"
    }
  }

  required_version = ">= 1.8.2"
}

provider "aws" {
    region = "us-east-1"
    profile = "default"
}

module "tic-tac-toe-application" {
    source = "./tf-modules"

    instance_type     = "t2.medium"
    key_private       = "key-tf"
    key_public        = "./keys/key-tf.pub"
}
