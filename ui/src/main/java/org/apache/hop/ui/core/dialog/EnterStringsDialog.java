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

package org.apache.hop.ui.core.dialog;

import org.apache.hop.core.Const;
import org.apache.hop.core.RowMetaAndData;
import org.apache.hop.core.exception.HopValueException;
import org.apache.hop.core.row.IValueMeta;
import org.apache.hop.core.row.value.ValueMetaString;
import org.apache.hop.core.util.Utils;
import org.apache.hop.core.variables.Variables;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.ui.core.PropsUi;
import org.apache.hop.ui.core.gui.WindowProperty;
import org.apache.hop.ui.core.widget.ColumnInfo;
import org.apache.hop.ui.core.widget.TableView;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;

/**
 * Shows a dialog that allows you to enter values for a number of strings.
 *
 * @author Matt
 */
public class EnterStringsDialog extends Dialog {
  private static final Class<?> PKG = EnterStringsDialog.class; // For Translator

  private TableView wFields;

  private Shell shell;
  private RowMetaAndData strings;
  private PropsUi props;

  private boolean readOnly;
  private String message;
  private String title;
  private Image shellImage;

  /**
   * Constructs a new dialog
   *
   * @param parent The parent shell to link to
   * @param style The style in which we want to draw this shell.
   * @param strings The list of rows to change.
   */
  public EnterStringsDialog(Shell parent, int style, RowMetaAndData strings) {
    super(parent, style);
    this.strings = strings;
    props = PropsUi.getInstance();
    readOnly = false;

    title = BaseMessages.getString(PKG, "EnterStringsDialog.Title");
    message = BaseMessages.getString(PKG, "EnterStringsDialog.Message");
  }

  public RowMetaAndData open() {
    Shell parent = getParent();

    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX);
    props.setLook(shell);

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout(formLayout);
    shell.setText(title);

    int margin = props.getMargin();

    // Buttons at the bottom
    //
    Button wOk = new Button(shell, SWT.PUSH);
    wOk.setText(BaseMessages.getString(PKG, "System.Button.OK"));
    wOk.addListener(SWT.Selection, e -> ok());
    Button wCancel = new Button(shell, SWT.PUSH);
    wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));
    wCancel.addListener(SWT.Selection, e -> cancel());
    BaseTransformDialog.positionBottomButtons(shell, new Button[] {wOk, wCancel}, margin, null);

    // Message line
    Label wlFields = new Label(shell, SWT.NONE);
    wlFields.setText(message);
    props.setLook(wlFields);
    FormData fdlFields = new FormData();
    fdlFields.left = new FormAttachment(0, 0);
    fdlFields.top = new FormAttachment(0, margin);
    wlFields.setLayoutData(fdlFields);

    int nrRows = strings.getRowMeta().size();

    ColumnInfo[] columns =
        new ColumnInfo[] {
          new ColumnInfo(
              BaseMessages.getString(PKG, "EnterStringsDialog.StringName.Label"),
              ColumnInfo.COLUMN_TYPE_TEXT,
              false,
              readOnly),
          new ColumnInfo(
              BaseMessages.getString(PKG, "EnterStringsDialog.StringValue.Label"),
              ColumnInfo.COLUMN_TYPE_TEXT,
              false,
              readOnly)
        };

    wFields =
        new TableView(
            Variables.getADefaultVariableSpace(),
            shell,
            SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI,
            columns,
            nrRows,
            null,
            props);
    wFields.setReadonly(readOnly);

    FormData fdFields = new FormData();
    fdFields.left = new FormAttachment(0, 0);
    fdFields.top = new FormAttachment(wlFields, 30);
    fdFields.right = new FormAttachment(100, 0);
    fdFields.bottom = new FormAttachment(wOk, -2 * margin);
    wFields.setLayoutData(fdFields);

    getData();

    if (shellImage != null) {
      shell.setImage(shellImage);
    }

    BaseDialog.defaultShellHandling(shell, c -> ok(), c -> cancel());

    return strings;
  }

  public void dispose() {
    props.setScreen(new WindowProperty(shell));
    shell.dispose();
  }

  /** Copy information from the meta-data input to the dialog fields. */
  public void getData() {
    if (strings != null) {
      for (int i = 0; i < strings.getRowMeta().size(); i++) {
        IValueMeta valueMeta = strings.getRowMeta().getValueMeta(i);
        Object valueData = strings.getData()[i];
        String string;
        try {
          string = valueMeta.getString(valueData);
        } catch (HopValueException e) {
          string = "";
          // TODO: can this ever be a meaningful exception? We're editing strings almost by
          // definition
        }
        TableItem item = wFields.table.getItem(i);
        item.setText(1, valueMeta.getName());
        if (!Utils.isEmpty(string)) {
          item.setText(2, string);
        }
      }
    }
    wFields.sortTable(1);
    wFields.setRowNums();
    wFields.optWidth(true);
  }

  private void cancel() {
    strings = null;
    dispose();
  }

  private void ok() {
    if (readOnly) {
      // Loop over the input rows and find the new values...
      int nrNonEmptyFields = wFields.nrNonEmpty();
      for (int i = 0; i < nrNonEmptyFields; i++) {
        TableItem item = wFields.getNonEmpty(i);
        String name = item.getText(1);
        for (int j = 0; j < strings.getRowMeta().size(); j++) {
          IValueMeta valueMeta = strings.getRowMeta().getValueMeta(j);

          if (valueMeta.getName().equalsIgnoreCase(name)) {
            String stringValue = item.getText(2);
            // CHECKSTYLE:Indentation:OFF
            strings.getData()[j] = stringValue;
          }
        }
      }
    } else {
      // Variable: re-construct the list of strings again...

      strings.clear();
      int nrNonEmptyFields = wFields.nrNonEmpty();
      for (int i = 0; i < nrNonEmptyFields; i++) {
        TableItem item = wFields.getNonEmpty(i);
        String name = item.getText(1);
        String value = item.getText(2);
        strings.addValue(new ValueMetaString(name), value);
      }
    }
    dispose();
  }

  /** @return Returns the readOnly. */
  public boolean isReadOnly() {
    return readOnly;
  }

  /** @param readOnly The readOnly to set. */
  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

  /** @return the message */
  public String getMessage() {
    return message;
  }

  /** @param message the message to set */
  public void setMessage(String message) {
    this.message = message;
  }

  /** @return the title */
  public String getTitle() {
    return title;
  }

  /** @param title the title to set */
  public void setTitle(String title) {
    this.title = title;
  }

  /** @param shellImage the shellImage to set */
  public void setShellImage(Image shellImage) {
    this.shellImage = shellImage;
  }
}
