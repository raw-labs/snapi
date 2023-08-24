/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.compiler.rql2.tests.regressions

import raw.compiler.SnapiInterpolator
import raw.creds.S3TestCreds
import raw.compiler.rql2.tests.CompilerTestContext

trait RD5932Test extends CompilerTestContext with S3TestCreds {

  val data = tempFile("""[
    |    {"id": 1, "network_interface": "eni-08b85cc07294f82bf"},
    |    {"id": 2, "network_interface": []},
    |    {"id": 3, "network_interface": null},
    |    {"id": 4}
    |]  """.stripMargin)

  s3Bucket(authorizedUser, UnitTestPrivateBucket)

  test(snapi"""Json.InferAndRead("$data")""")(it => it should run)

  test(
    snapi"""let
      |     data = Json.Read(
      |        "$data",
      |        type collection(record(id: int, network_interface: string or collection(undefined)))
      |     )
      |in
      |     Collection.Filter(data, x -> Try.IsError(x.network_interface))
      |    """.stripMargin
  )(it => it should evaluateTo(""" [] """.stripMargin))

  test(
    snapi"""let
      |     data = Json.Read(
      |        "$data",
      |        type collection(record(id: int, network_interface: string or collection(undefined)))
      |     )
      |in
      |    data
      |    """.stripMargin
  )(it => it should run)

  test("""Json.InferAndRead("s3://rawlabs-private-test-data/rd-5932-dimitris-backend.json")""") {
    _ should run
  }

  val jsonType = """record(
    |    version: int,
    |    terraform_version: string,
    |    serial: int,
    |    lineage: string,
    |    outputs: record(
    |        instance_id: record(value: string, `type`: string),
    |        instance_public_ip: record(value: string, `type`: string),
    |        root_url: record(value: string, `type`: string)),
    |    resources: collection(
    |        record(
    |            module: string,
    |            mode: string,
    |            `type`: string,
    |            name: string,
    |            provider: string,
    |            instances: collection(
    |                record(
    |                    schema_version: int,
    |                    attributes: record(
    |                        id: string,
    |                        keepers: undefined,
    |                        length: int,
    |                        lower: bool,
    |                        min_lower: int,
    |                        min_numeric: int,
    |                        min_special: int,
    |                        min_upper: int,
    |                        number: bool,
    |                        override_special: undefined,
    |                        result: string,
    |                        special: bool,
    |                        upper: bool,
    |                        device_name: string,
    |                        force_detach: undefined,
    |                        instance_id: string,
    |                        skip_destroy: undefined,
    |                        stop_instance_before_detaching: undefined,
    |                        volume_id: string,
    |                        arn: string,
    |                        confirmation_timeout_in_minutes: int,
    |                        confirmation_was_authenticated: bool,
    |                        delivery_policy: string,
    |                        endpoint: string,
    |                        endpoint_auto_confirms: bool,
    |                        filter_policy: string,
    |                        owner_id: string,
    |                        pending_confirmation: bool,
    |                        protocol: string,
    |                        raw_message_delivery: bool,
    |                        redrive_policy: string,
    |                        subscription_role_arn: string,
    |                        topic_arn: string,
    |                        application_failure_feedback_role_arn: string,
    |                        application_success_feedback_role_arn: string,
    |                        application_success_feedback_sample_rate: undefined,
    |                        content_based_deduplication: bool,
    |                        display_name: string,
    |                        fifo_topic: bool,
    |                        firehose_failure_feedback_role_arn: string,
    |                        firehose_success_feedback_role_arn: string,
    |                        firehose_success_feedback_sample_rate: undefined,
    |                        http_failure_feedback_role_arn: string,
    |                        http_success_feedback_role_arn: string,
    |                        http_success_feedback_sample_rate: undefined,
    |                        kms_master_key_id: string,
    |                        lambda_failure_feedback_role_arn: string,
    |                        lambda_success_feedback_role_arn: string,
    |                        lambda_success_feedback_sample_rate: undefined,
    |                        name: string,
    |                        name_prefix: string,
    |                        owner: string,
    |                        policy: string,
    |                        sqs_failure_feedback_role_arn: string,
    |                        sqs_success_feedback_role_arn: string,
    |                        sqs_success_feedback_sample_rate: undefined,
    |                        tags: record(
    |                            Name: string,
    |                            Env: string,
    |                            ManagedBy: string,
    |                            main: string),
    |                        tags_all: record(Name: string, Env: string),
    |                        alias: collection(undefined),
    |                        allow_overwrite: undefined,
    |                        failover_routing_policy: collection(undefined),
    |                        fqdn: string,
    |                        geolocation_routing_policy: collection(undefined),
    |                        health_check_id: string,
    |                        latency_routing_policy: collection(undefined),
    |                        multivalue_answer_routing_policy: undefined,
    |                        records: collection(string),
    |                        set_identifier: string,
    |                        ttl: int,
    |                        `type`: string,
    |                        weighted_routing_policy: collection(undefined),
    |                        zone_id: string,
    |                        child_health_threshold: int,
    |                        child_healthchecks: undefined,
    |                        cloudwatch_alarm_name: undefined,
    |                        cloudwatch_alarm_region: undefined,
    |                        disabled: bool,
    |                        enable_sni: bool,
    |                        failure_threshold: int,
    |                        insufficient_data_health_status: string,
    |                        invert_healthcheck: bool,
    |                        ip_address: string,
    |                        measure_latency: bool,
    |                        port: int,
    |                        reference_name: undefined,
    |                        regions: undefined,
    |                        request_interval: int,
    |                        resource_path: string,
    |                        routing_control_arn: string,
    |                        search_string: string,
    |                        ami: string,
    |                        associate_public_ip_address: bool,
    |                        availability_zone: string,
    |                        capacity_reservation_specification: collection(
    |                            record(
    |                                capacity_reservation_preference: string,
    |                                capacity_reservation_target: collection(
    |                                    undefined))),
    |                        cpu_core_count: int,
    |                        cpu_threads_per_core: int,
    |                        credit_specification: collection(undefined),
    |                        disable_api_termination: bool,
    |                        ebs_block_device: collection(
    |                            record(
    |                                delete_on_termination: bool,
    |                                device_name: string,
    |                                encrypted: bool,
    |                                iops: int,
    |                                kms_key_id: string,
    |                                snapshot_id: string,
    |                                tags: record(Name: string),
    |                                throughput: int,
    |                                volume_id: string,
    |                                volume_size: int,
    |                                volume_type: string)),
    |                        ebs_optimized: bool,
    |                        enclave_options: collection(record(enabled: bool)),
    |                        ephemeral_block_device: collection(undefined),
    |                        get_password_data: bool,
    |                        hibernation: bool,
    |                        host_id: undefined,
    |                        iam_instance_profile: string,
    |                        instance_initiated_shutdown_behavior: string,
    |                        instance_state: string,
    |                        instance_type: string,
    |                        ipv6_address_count: int,
    |                        ipv6_addresses: collection(undefined),
    |                        key_name: string,
    |                        launch_template: collection(undefined),
    |                        metadata_options: collection(
    |                            record(
    |                                http_endpoint: string,
    |                                http_put_response_hop_limit: int,
    |                                http_tokens: string)),
    |                        monitoring: bool,
    |                        network_interface: string or collection(undefined),
    |                        outpost_arn: string,
    |                        password_data: string,
    |                        placement_group: string,
    |                        placement_partition_number: undefined,
    |                        primary_network_interface_id: string,
    |                        private_dns: string,
    |                        private_ip: string,
    |                        public_dns: string,
    |                        public_ip: string,
    |                        root_block_device: collection(
    |                            record(
    |                                delete_on_termination: bool,
    |                                device_name: string,
    |                                encrypted: bool,
    |                                iops: int,
    |                                kms_key_id: string,
    |                                tags: record(Name: string),
    |                                throughput: int,
    |                                volume_id: string,
    |                                volume_size: int,
    |                                volume_type: string)),
    |                        secondary_private_ips: collection(undefined),
    |                        security_groups: collection(string),
    |                        source_dest_check: bool,
    |                        subnet_id: string,
    |                        tenancy: string,
    |                        timeouts: undefined,
    |                        user_data: undefined,
    |                        user_data_base64: undefined,
    |                        volume_tags: undefined,
    |                        vpc_security_group_ids: collection(string),
    |                        policy_arn: string,
    |                        role: string,
    |                        assume_role_policy: string,
    |                        create_date: string,
    |                        description: string,
    |                        force_detach_policies: bool,
    |                        inline_policy: collection(
    |                            record(name: string, policy: string)),
    |                        managed_policy_arns: collection(string),
    |                        max_session_duration: int,
    |                        path: string,
    |                        permissions_boundary: undefined,
    |                        unique_id: string,
    |                        address: undefined,
    |                        allocation_id: string,
    |                        associate_with_private_ip: undefined,
    |                        association_id: string,
    |                        carrier_ip: string,
    |                        customer_owned_ip: string,
    |                        customer_owned_ipv4_pool: string,
    |                        domain: string,
    |                        instance: string,
    |                        network_border_group: string,
    |                        public_ipv4_pool: string,
    |                        vpc: bool,
    |                        encrypted: bool,
    |                        iops: int,
    |                        kms_key_id: string,
    |                        multi_attach_enabled: bool,
    |                        size: int,
    |                        snapshot_id: string,
    |                        throughput: int,
    |                        actions_enabled: bool,
    |                        alarm_actions: collection(string),
    |                        alarm_description: string,
    |                        alarm_name: string,
    |                        comparison_operator: string,
    |                        datapoints_to_alarm: int,
    |                        dimensions: record(HealthCheckId: string),
    |                        evaluate_low_sample_count_percentiles: string,
    |                        evaluation_periods: int,
    |                        extended_statistic: string,
    |                        insufficient_data_actions: collection(undefined),
    |                        metric_name: string,
    |                        metric_query: collection(undefined),
    |                        namespace: string,
    |                        ok_actions: collection(undefined),
    |                        period: int,
    |                        statistic: string,
    |                        threshold: int,
    |                        threshold_metric_id: string,
    |                        treat_missing_data: string,
    |                        unit: string,
    |                        caller_reference: string,
    |                        comment: string,
    |                        linked_service_description: undefined,
    |                        linked_service_principal: undefined,
    |                        name_servers: collection(string),
    |                        private_zone: bool,
    |                        resource_record_set_count: int,
    |                        vpc_id: undefined),
    |                    sensitive_attributes: collection(undefined),
    |                    private: string,
    |                    index_key: int,
    |                    dependencies: collection(string))))))""".stripMargin

  test(s"""Json.Read("s3://rawlabs-private-test-data/rd-5932-dimitris-backend.json", type $jsonType)""") {
    _ should run
  }
}
