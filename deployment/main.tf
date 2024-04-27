module "tic-tac-toe-application" {
    source = "./tf-modules"

    instance_type   = "t2.medium"
    key_name        = "key-tf.pem"
}
