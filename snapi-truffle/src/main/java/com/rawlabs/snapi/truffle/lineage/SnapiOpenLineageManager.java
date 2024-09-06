/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.truffle.lineage;

import io.openlineage.client.OpenLineage;
import io.openlineage.client.OpenLineageClientUtils;
import io.openlineage.client.transports.HttpConfig;
import io.openlineage.client.transports.HttpTransport;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SnapiOpenLineageManager {
  URI producer = URI.create("http://my.producer/uri");
  ZonedDateTime now = ZonedDateTime.now();
  OpenLineage ol = new OpenLineage(producer);

  public SnapiOpenLineageManager() {}

  public String toJson(Object obj) {
    return OpenLineageClientUtils.toJson(obj);
  }

  private void sendDataset(OpenLineage.DatasetEvent datasetEvent) {
    try {
      HttpConfig config = new HttpConfig();
      config.setUrl(new URI("http://localhost:5000/api/v1/lineage"));
      try (HttpTransport transport = new HttpTransport(config)) {
        transport.emit(datasetEvent);
      } catch (IOException ignored) {
      }
    } catch (URISyntaxException ignored) {
    }
  }

  private void sendRun(OpenLineage.RunEvent runEvent) {
    try {
      HttpConfig config = new HttpConfig();
      config.setUrl(new URI("http://localhost:5000/api/v1/lineage"));
      try (HttpTransport transport = new HttpTransport(config)) {
        transport.emit(runEvent);
      } catch (IOException ignored) {
        System.out.println("Error sending run event");
      }
    } catch (URISyntaxException ignored) {
    }
  }

  public OpenLineage.StaticDataset getStaticDataset(int index) {

    OpenLineage.ColumnLineageDatasetFacetFields fields = ol.newColumnLineageDatasetFacetFields();

    OpenLineage.ColumnLineageDatasetFacetFieldsAdditionalInputFields inputFields =
        new OpenLineage.ColumnLineageDatasetFacetFieldsAdditionalInputFieldsBuilder()
            .field("column" + index)
            .name("dataset" + index)
            .build();

    OpenLineage.ColumnLineageDatasetFacetFieldsAdditional additional =
        new OpenLineage.ColumnLineageDatasetFacetFieldsAdditionalBuilder()
            .transformationDescription("projecting column" + index)
            .inputFields(List.of(inputFields))
            .build();
    fields.getAdditionalProperties().put("column" + index, additional);

    OpenLineage.ColumnLineageDatasetFacet datasetFacet = ol.newColumnLineageDatasetFacet(fields);

    OpenLineage.DatasetFacets datasetFacets =
        new OpenLineage.DatasetFacetsBuilder().put("columnLineage" + index, datasetFacet).build();

    return new OpenLineage.StaticDatasetBuilder()
        .name("dataset" + index)
        .namespace("acme")
        .facets(datasetFacets)
        .build();

    //    OpenLineage.DatasetEventBuilder deb =
    // ol.newDatasetEventBuilder().dataset(ds).eventTime(now);
    //    return deb.build();
  }

  public OpenLineage.InputDataset getInputDataset(int index) {

    OpenLineage.ColumnLineageDatasetFacetFields fields = ol.newColumnLineageDatasetFacetFields();

    OpenLineage.ColumnLineageDatasetFacetFieldsAdditionalInputFields inputFields =
        new OpenLineage.ColumnLineageDatasetFacetFieldsAdditionalInputFieldsBuilder()
            .field("column" + index)
            .name("dataset" + index)
            .build();

    OpenLineage.ColumnLineageDatasetFacetFieldsAdditional additional =
        new OpenLineage.ColumnLineageDatasetFacetFieldsAdditionalBuilder()
            .transformationDescription("projecting column" + index)
            .inputFields(List.of(inputFields))
            .build();
    fields.getAdditionalProperties().put("column" + index, additional);

    OpenLineage.ColumnLineageDatasetFacet datasetFacet = ol.newColumnLineageDatasetFacet(fields);

    OpenLineage.DatasetFacets datasetFacets =
        new OpenLineage.DatasetFacetsBuilder().put("columnLineage" + index, datasetFacet).build();

    return new OpenLineage.InputDatasetBuilder()
        .name("dataset" + index)
        .namespace("acme")
        .facets(datasetFacets)
        .build();

    //    OpenLineage.DatasetEventBuilder deb =
    // ol.newDatasetEventBuilder().dataset(ds).eventTime(now);
    //    return deb.build();
  }

  public OpenLineage.OutputDataset getOutputDataset(int index) {

    OpenLineage.ColumnLineageDatasetFacetFields fields = ol.newColumnLineageDatasetFacetFields();

    OpenLineage.ColumnLineageDatasetFacetFieldsAdditionalInputFields inputFields =
        new OpenLineage.ColumnLineageDatasetFacetFieldsAdditionalInputFieldsBuilder()
            .field("column" + index)
            .name("dataset" + index)
            .build();

    OpenLineage.ColumnLineageDatasetFacetFieldsAdditional additional =
        new OpenLineage.ColumnLineageDatasetFacetFieldsAdditionalBuilder()
            .transformationDescription("projecting column" + index)
            .inputFields(List.of(inputFields))
            .build();
    fields.getAdditionalProperties().put("column" + index, additional);

    OpenLineage.ColumnLineageDatasetFacet datasetFacet = ol.newColumnLineageDatasetFacet(fields);

    OpenLineage.DatasetFacets datasetFacets =
        new OpenLineage.DatasetFacetsBuilder()
            .put("columnLineage" + index, datasetFacet)
            .columnLineage(datasetFacet)
            .build();

    return new OpenLineage.OutputDatasetBuilder()
        .name("dataset" + index)
        .namespace("acme")
        .facets(datasetFacets)
        .build();

    //    OpenLineage.DatasetEventBuilder deb =
    // ol.newDatasetEventBuilder().dataset(ds).eventTime(now);
    //    return deb.build();
  }

  public void addRun() {

    OpenLineage.InputDataset dataset1 = getInputDataset(1);
    OpenLineage.InputDataset dataset2 = getInputDataset(2);

    OpenLineage.OutputDataset outputDataset = getOutputDataset(3);

    OpenLineage.RunFacets runFacets = ol.newRunFacetsBuilder().build();
    OpenLineage.JobFacets jobFacets = ol.newJobFacetsBuilder().build();
    OpenLineage.Run run = ol.newRunBuilder().facets(runFacets).runId(UUID.randomUUID()).build();

    OpenLineage.Job job =
        ol.newJobBuilder().namespace("acme").name("acmeJob").facets(jobFacets).build();

    OpenLineage.RunEvent runEvent =
        ol.newRunEventBuilder()
            .eventTime(now)
            .run(run)
            .job(job)
            .inputs(Arrays.asList(dataset1, dataset2))
            .outputs(Collections.singletonList(outputDataset))
            .eventType(OpenLineage.RunEvent.EventType.START)
            .build();
    sendRun(runEvent);
    OpenLineage.RunEvent runEvent2 =
        ol.newRunEventBuilder()
            .eventTime(now)
            .run(run)
            .job(job)
            .inputs(Arrays.asList(dataset1, dataset2))
            .outputs(Collections.singletonList(outputDataset))
            .eventType(OpenLineage.RunEvent.EventType.COMPLETE)
            .build();
    sendRun(runEvent2);
  }
}
