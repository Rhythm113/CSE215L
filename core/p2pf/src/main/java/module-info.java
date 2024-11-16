module com.p2p.share.p2pf {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.p2p.share.p2pf to javafx.fxml;
    exports com.p2p.share.p2pf;
}