# Throwaway root whose only job is to prove the state backend works before
# OpenTofu is allowed anywhere near a live Confitura service.
#
# Run it first, against the empty bucket:
#
#   export AWS_ACCESS_KEY_ID=...            # B2 application key id
#   export AWS_SECRET_ACCESS_KEY=...        # B2 application key
#   export AWS_REQUEST_CHECKSUM_CALCULATION=when_required
#   export AWS_RESPONSE_CHECKSUM_VALIDATION=when_required
#
#   tofu init
#   tofu apply -lock=false
#   tofu destroy -lock=false
#
# -lock=false is required: B2 cannot lock. See infra/production/versions.tf.
#
# If init or the first write fails, it fails here against an empty bucket
# instead of halfway through importing production. Delete this directory once
# the production root has its state in the bucket.

terraform {
  required_version = ">= 1.6.0"

  backend "s3" {
    bucket = "bcc-opentofu-coolify"
    key    = "confitura/backend-proof/terraform.tfstate"
    region = "eu-central-003"

    endpoints = {
      s3 = "https://s3.eu-central-003.backblazeb2.com"
    }

    skip_credentials_validation = true
    skip_region_validation      = true
    skip_metadata_api_check     = true
    skip_requesting_account_id  = true
    skip_s3_checksum            = true
  }
}

# terraform_data is built in, so this root needs no provider and touches
# nothing outside the state bucket.
resource "terraform_data" "proof" {
  input = "backend reachable and writable"
}

output "proof" {
  value = terraform_data.proof.output
}
