# Adopt the backend environment variables that already exist in Coolify.
#
# The 10 variables below were renamed by hand in Coolify (dotted Spring names to
# relaxed-binding names) before they were declared in production.auto.tfvars.
# Without these blocks OpenTofu plans to create them, and Coolify rejects each
# one because the key already exists. An import block makes the plan read
# "to import" instead, and the apply adopts them into state.
# CONFERENCE_C4P_START and _END were adopted too and later removed, once the
# backend read the C4P period from the Current Edition.
#
# MANAGEMENT_OPENTELEMETRY_TRACING_EXPORT_OTLP_ENDPOINT was renamed in place from
# its lower-case form, so it keeps the same variable id.
#
# Once the variables are in state these blocks are no-ops. They may be removed
# in a later change.
#
# Deliberately absent: the secrets APP_JWT_SECRETKEY (dh3evqv0icsy97n5n3ylj8xh)
# and LISTMONK_PASSWORD (rmvu1gfbvh80ubi4161lgzmz). An import block whose target
# has no configuration fails the plan, and these keys only get configuration
# (under coolify_environment_variable.secret) once they are added to
# secrets.enc.yaml. Add their import blocks in the same PR as that sops change.

import {
  to = module.app["backend"].coolify_environment_variable.plain["APP_CORS_ORIGINS_0_"]
  id = "application:wqwwmtfjn4xuhsgj4uzwfd4x:ohmryaesleeafvlwtwt65ns1"
}

import {
  to = module.app["backend"].coolify_environment_variable.plain["APP_CORS_ORIGINS_1_"]
  id = "application:wqwwmtfjn4xuhsgj4uzwfd4x:sktve4u7ooep50r7hk287ct0"
}

import {
  to = module.app["backend"].coolify_environment_variable.plain["APP_CORS_ORIGINS_2_"]
  id = "application:wqwwmtfjn4xuhsgj4uzwfd4x:wcv9bprpg15l0ht201x9kep6"
}

import {
  to = module.app["backend"].coolify_environment_variable.plain["APP_CORS_ORIGINS_3_"]
  id = "application:wqwwmtfjn4xuhsgj4uzwfd4x:i12qwj0qm52qqodn0bkbxcq3"
}

import {
  to = module.app["backend"].coolify_environment_variable.plain["LISTMONK_BASE_URL"]
  id = "application:wqwwmtfjn4xuhsgj4uzwfd4x:d13ehyun9ukki01rfqhoewiw"
}

import {
  to = module.app["backend"].coolify_environment_variable.plain["LISTMONK_USERNAME"]
  id = "application:wqwwmtfjn4xuhsgj4uzwfd4x:k14mx9ytxm55b3qkgukoaw69"
}

import {
  to = module.app["backend"].coolify_environment_variable.plain["RESOURCES_RESOURCES_BASE_URL"]
  id = "application:wqwwmtfjn4xuhsgj4uzwfd4x:e12ewha3dwrs82ugrswmq1fj"
}

import {
  to = module.app["backend"].coolify_environment_variable.plain["SPRING_DATASOURCE_URL"]
  id = "application:wqwwmtfjn4xuhsgj4uzwfd4x:ptaxhuphhfplelcgadkahuyj"
}

import {
  to = module.app["backend"].coolify_environment_variable.plain["SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE"]
  id = "application:wqwwmtfjn4xuhsgj4uzwfd4x:dlnuafr4zpua0419cbabb5eb"
}

import {
  to = module.app["backend"].coolify_environment_variable.plain["MANAGEMENT_OPENTELEMETRY_TRACING_EXPORT_OTLP_ENDPOINT"]
  id = "application:wqwwmtfjn4xuhsgj4uzwfd4x:hc18ch7uufmm6rre8m41fb5z"
}
