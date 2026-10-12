terraform {
  required_version = ">= 1.6.0"

  required_providers {
    coolify = {
      # Verified against the OpenTofu and Terraform registries on 2026-10-05:
      # latest 0.1.25, 26 releases, actively maintained. No competing fork.
      # Pinned narrowly on purpose - this is a 0.x provider, so a minor bump
      # can change resource schemas.
      source  = "coolify-terraform/coolify"
      version = "~> 0.1.25"
    }

    sops = {
      source  = "carlpett/sops"
      version = "~> 1.2"
    }
  }

  # Backblaze B2 over its S3-compatible API.
  #
  # There is no state lock. B2 answers the conditional PutObject that the
  # S3-native lock needs with "501 not implemented", and DynamoDB locking is
  # AWS-only, so the backend has no lock to fall back on. The owner decided on
  # 2026-10-05 to stay on B2 and serialise applies in the pipeline instead:
  # infra-apply.yml carries concurrency group tofu-apply-production with
  # cancel-in-progress false, and runs pass -lock=false. Bucket versioning is
  # on; Object Lock must stay off.
  #
  # Credentials come from AWS_ACCESS_KEY_ID / AWS_SECRET_ACCESS_KEY in the
  # environment. Never in this file.
  #
  # Also required in the environment, locally and in CI:
  #   AWS_REQUEST_CHECKSUM_CALCULATION=when_required
  #   AWS_RESPONSE_CHECKSUM_VALIDATION=when_required
  # Recent AWS SDK releases add checksum headers by default and B2 rejects
  # them; skip_s3_checksum alone has not been enough in reported cases.
  backend "s3" {
    bucket = "bcc-opentofu-coolify"
    key    = "confitura/production/terraform.tfstate"
    region = "eu-central-003"

    endpoints = {
      s3 = "https://s3.eu-central-003.backblazeb2.com"
    }

    # Every one of these is needed. B2 is not Amazon S3: its key format and
    # region names fail AWS validation, it has no instance metadata service,
    # and it has no account-id endpoint.
    skip_credentials_validation = true
    skip_region_validation      = true
    skip_metadata_api_check     = true
    skip_requesting_account_id  = true
    skip_s3_checksum            = true

    # Do not set use_lockfile. See the note above.
  }
}
