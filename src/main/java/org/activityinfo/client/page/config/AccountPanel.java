package org.activityinfo.client.page.config;

import org.activityinfo.client.Application;

import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.core.client.GWT;

public class AccountPanel extends FormPanel implements AccountEditor.View {
	
	public AccountPanel() {
	
		this.setHeading(Application.CONSTANTS.mySettings());

        FieldSet userInfoSet = new FieldSet();
        userInfoSet.setHeading(Application.CONSTANTS.userInformation());

        TextField<String> userNameField = new TextField<String>();
        userNameField.setFieldLabel("Votre nom");
        userInfoSet.add(userNameField);

        TextField<String> userEmailField = new TextField<String>();
        userEmailField.setFieldLabel("Votre email");
        userInfoSet.add(userEmailField);

        ComboBox localeCombo = new ComboBox();
        localeCombo.setFieldLabel("Locale");
        localeCombo.setDisplayField("name");
        localeCombo.setValueField("code");

       // this.add(userInfoSet);

		
	}
	

}