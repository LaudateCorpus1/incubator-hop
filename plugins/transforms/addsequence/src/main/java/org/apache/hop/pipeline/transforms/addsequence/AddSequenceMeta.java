/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hop.pipeline.transforms.addsequence;

import org.apache.hop.core.CheckResult;
import org.apache.hop.core.Const;
import org.apache.hop.core.ICheckResult;
import org.apache.hop.core.SqlStatement;
import org.apache.hop.core.annotations.Transform;
import org.apache.hop.core.database.Database;
import org.apache.hop.core.database.DatabaseMeta;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.IValueMeta;
import org.apache.hop.core.row.value.ValueMetaInteger;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.pipeline.Pipeline;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.transform.BaseTransformMeta;
import org.apache.hop.pipeline.transform.ITransformMeta;
import org.apache.hop.pipeline.transform.TransformMeta;

import java.util.List;

/** Meta data for the Add Sequence transform. */
@Transform(
    id = "Sequence",
    image = "addsequence.svg",
    name = "i18n::BaseTransform.TypeLongDesc.AddSequence",
    description = "i18n::BaseTransform.TypeTooltipDesc.AddSequence",
    categoryDescription = "i18n:org.apache.hop.pipeline.transform:BaseTransform.Category.Transform",
    documentationUrl = "/pipeline/transforms/addsequence.html",
    keywords = "i18n::AddSequenceMeta.keyword")
public class AddSequenceMeta extends BaseTransformMeta
    implements ITransformMeta<AddSequence, AddSequenceData> {

  private static final Class<?> PKG = AddSequenceMeta.class; // For Translator

  @HopMetadataProperty(
      key = "valuename",
      injectionKeyDescription = "AddSequenceDialog.Valuename.Label")
  private String valueName;

  @HopMetadataProperty(
      key = "use_database",
      injectionKeyDescription = "AddSequenceDialog.UseDatabase.Label")
  private boolean databaseUsed;

  @HopMetadataProperty(
      key = "connection",
      injectionKeyDescription = "AddSequenceMeta.Injection.Connection")
  private String connection;

  @HopMetadataProperty(
      key = "schema",
      injectionKeyDescription = "AddSequenceMeta.Injection.SchemaName")
  private String schemaName;

  @HopMetadataProperty(
      key = "seqname",
      injectionKeyDescription = "AddSequenceMeta.Injection.SequenceName")
  private String sequenceName;

  @HopMetadataProperty(
      key = "use_counter",
      injectionKeyDescription = "AddSequenceMeta.Injection.UseCounter")
  private boolean counterUsed;

  @HopMetadataProperty(
      key = "counter_name",
      injectionKeyDescription = "AddSequenceMeta.Injection.CounterName")
  private String counterName;

  @HopMetadataProperty(
      key = "start_at",
      injectionKeyDescription = "AddSequenceMeta.Injection.StartAt")
  private String startAt;

  @HopMetadataProperty(
      key = "increment_by",
      injectionKeyDescription = "AddSequenceMeta.Injection.IncrementBy")
  private String incrementBy;

  @HopMetadataProperty(
      key = "max_value",
      injectionKeyDescription = "AddSequenceMeta.Injection.MaxValue")
  private String maxValue;

  public String getConnection() {
    return connection;
  }

  public void setConnection(String connection) {
    this.connection = connection;
  }

  /** @return Returns the incrementBy. */
  public String getIncrementBy() {
    return incrementBy;
  }

  /** @param incrementBy The incrementBy to set. */
  public void setIncrementBy(String incrementBy) {
    this.incrementBy = incrementBy;
  }

  /** @return Returns the maxValue. */
  public String getMaxValue() {
    return maxValue;
  }

  /** @param maxValue The maxValue to set. */
  public void setMaxValue(String maxValue) {
    this.maxValue = maxValue;
  }

  /** @return Returns the sequenceName. */
  public String getSequenceName() {
    return sequenceName;
  }

  /** @param sequenceName The sequenceName to set. */
  public void setSequenceName(String sequenceName) {
    this.sequenceName = sequenceName;
  }

  /** @param maxValue The maxValue to set. */
  public void setMaxValue(long maxValue) {
    this.maxValue = Long.toString(maxValue);
  }

  /** @param startAt The starting point of the sequence to set. */
  public void setStartAt(long startAt) {
    this.startAt = Long.toString(startAt);
  }

  /** @param incrementBy The incrementBy to set. */
  public void setIncrementBy(long incrementBy) {
    this.incrementBy = Long.toString(incrementBy);
  }

  /** @return Returns the start of the sequence. */
  public String getStartAt() {
    return startAt;
  }

  /** @param startAt The starting point of the sequence to set. */
  public void setStartAt(String startAt) {
    this.startAt = startAt;
  }

  /** @return Returns the useCounter. */
  public boolean isCounterUsed() {
    return counterUsed;
  }

  /** @param counterUsed The useCounter to set. */
  public void setCounterUsed(boolean counterUsed) {
    this.counterUsed = counterUsed;
  }

  /** @return Returns the useDatabase. */
  public boolean isDatabaseUsed() {
    return databaseUsed;
  }

  /** @param databaseUsed The useDatabase to set. */
  public void setDatabaseUsed(boolean databaseUsed) {
    this.databaseUsed = databaseUsed;
  }

  /** @return Returns the valuename. */
  public String getValueName() {
    return valueName;
  }

  /** @param valueName The valuename to set. */
  public void setValueName(String valueName) {
    this.valueName = valueName;
  }

  @Override
  public Object clone() {
    return super.clone();
  }

  @Override
  public void setDefault() {
    valueName = "valuename";

    databaseUsed = false;
    schemaName = "";
    sequenceName = "SEQ_";
    counterUsed = true;
    counterName = null;
    startAt = "1";
    incrementBy = "1";
    maxValue = "999999999";
  }

  @Override
  public void getFields(
      IRowMeta row,
      String name,
      IRowMeta[] info,
      TransformMeta nextTransform,
      IVariables variables,
      IHopMetadataProvider metadataProvider) {
    IValueMeta v = new ValueMetaInteger(valueName);
    v.setOrigin(name);
    row.addValueMeta(v);
  }

  @Override
  public void check(
      List<ICheckResult> remarks,
      PipelineMeta pipelineMeta,
      TransformMeta transformMeta,
      IRowMeta prev,
      String[] input,
      String[] output,
      IRowMeta info,
      IVariables variables,
      IHopMetadataProvider metadataProvider) {
    CheckResult cr;
    Database db = null;

    try {
      DatabaseMeta databaseMeta =
          metadataProvider.getSerializer(DatabaseMeta.class).load(variables.resolve(connection));

      if (databaseUsed) {
        db = new Database(loggingObject, variables, databaseMeta);
        db.connect();
        if (db.checkSequenceExists(
            variables.resolve(schemaName), variables.resolve(sequenceName))) {
          cr =
              new CheckResult(
                  ICheckResult.TYPE_RESULT_OK,
                  BaseMessages.getString(PKG, "AddSequenceMeta.CheckResult.SequenceExists.Title"),
                  transformMeta);
        } else {
          cr =
              new CheckResult(
                  ICheckResult.TYPE_RESULT_ERROR,
                  BaseMessages.getString(
                      PKG,
                      "AddSequenceMeta.CheckResult.SequenceCouldNotBeFound.Title",
                      sequenceName),
                  transformMeta);
        }
        remarks.add(cr);
      }
    } catch (HopException e) {
      cr =
          new CheckResult(
              ICheckResult.TYPE_RESULT_ERROR,
              BaseMessages.getString(PKG, "AddSequenceMeta.CheckResult.UnableToConnectDB.Title")
                  + Const.CR
                  + e.getMessage(),
              transformMeta);
      remarks.add(cr);
    } finally {
      if (db != null) {
        db.disconnect();
      }
    }

    if (input.length > 0) {
      cr =
          new CheckResult(
              ICheckResult.TYPE_RESULT_OK,
              BaseMessages.getString(PKG, "AddSequenceMeta.CheckResult.TransformIsReceving.Title"),
              transformMeta);
      remarks.add(cr);
    } else {
      cr =
          new CheckResult(
              ICheckResult.TYPE_RESULT_ERROR,
              BaseMessages.getString(PKG, "AddSequenceMeta.CheckResult.NoInputReceived.Title"),
              transformMeta);
      remarks.add(cr);
    }
  }

  @Override
  public SqlStatement getSqlStatements(
      IVariables variables,
      PipelineMeta pipelineMeta,
      TransformMeta transformMeta,
      IRowMeta prev,
      IHopMetadataProvider metadataProvider) {
    Database db = null;
    SqlStatement retval = null;
    try {
      DatabaseMeta databaseMeta =
          metadataProvider.getSerializer(DatabaseMeta.class).load(variables.resolve(connection));
      retval = new SqlStatement(transformMeta.getName(), databaseMeta, null);
      // default: nothing to do!
      if (databaseUsed) {
        // Otherwise, don't bother!
        if (databaseMeta != null) {
          db = new Database(loggingObject, variables, databaseMeta);
          db.connect();
          if (!db.checkSequenceExists(schemaName, sequenceName)) {
            String crTable =
                db.getCreateSequenceStatement(sequenceName, startAt, incrementBy, maxValue, true);
            retval.setSql(crTable);
          } else {
            retval.setSql(null); // Empty string means: nothing to do: set it to null...
          }
        } else {
          retval.setError(
              BaseMessages.getString(PKG, "AddSequenceMeta.ErrorMessage.NoConnectionDefined"));
        }
      }
    } catch (HopException e) {
      if (retval != null) {
        retval.setError(
            BaseMessages.getString(PKG, "AddSequenceMeta.ErrorMessage.UnableToConnectDB")
                + Const.CR
                + e.getMessage());
      }
    } finally {
      if (db != null) {
        db.disconnect();
      }
    }

    return retval;
  }

  @Override
  public AddSequence createTransform(
      TransformMeta transformMeta,
      AddSequenceData data,
      int cnr,
      PipelineMeta pipelineMeta,
      Pipeline pipeline) {
    return new AddSequence(transformMeta, this, data, cnr, pipelineMeta, pipeline);
  }

  @Override
  public AddSequenceData getTransformData() {
    return new AddSequenceData();
  }

  /** @return the counterName */
  public String getCounterName() {
    return counterName;
  }

  /** @param counterName the counterName to set */
  public void setCounterName(String counterName) {
    this.counterName = counterName;
  }

  /** @return the schemaName */
  public String getSchemaName() {
    return schemaName;
  }

  /** @param schemaName the schemaName to set */
  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }
}
