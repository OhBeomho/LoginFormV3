module LoginFormV3 {
	requires javafx.controls;
	requires animatednodes;

	opens v3.form.login to javafx.graphics, javafx.fxml;
}
