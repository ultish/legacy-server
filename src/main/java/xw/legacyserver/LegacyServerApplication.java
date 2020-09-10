package xw.legacyserver;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.EnversSchemaGenerator;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import xw.legacyserver.entities.ChargeCode;
import xw.legacyserver.entities.TimeBlock;
import xw.legacyserver.entities.TrackedTask;
import xw.legacyserver.entities.User;

@SpringBootApplication
public class LegacyServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(LegacyServerApplication.class, args);

        if (args.length > 0) {
            boolean export = Boolean.parseBoolean(args[0]);
            if (export) {
                exportSchema();
            }
        }
    }

    private static void exportSchema() {

        Configuration config = new Configuration();

        //make sure you set the dialect correctly for your database (oracle
        // for example below)
        //        config.setProperty(
        //            "hibernate.dialect",
        //            "org.hibernate.dialect.PostgreSQL9Dialect"
        //        );
        config.setProperty(
            "hibernate.dialect",
            "org.hibernate.dialect.PostgreSQL82Dialect"
        );
        config.setProperty("org.hibernate.envers.audit_table_prefix", "aud_");
        config.setProperty("org.hibernate.envers.audit_table_suffix", "");
        //        config.setProperty(
        //            "org.hibernate.envers.global_with_modified_flag",
        //            "true"
        //        );
        config.setProperty(
            "org.hibernate.envers.revision_type_field_name",
            "revtype"
        );
        config.setProperty("org.hibernate.envers.revision_field_name", "rev");
        config.setProperty("org.hibernate.envers.modified_flag_suffix", "_mod");
        config.setProperty("org.hibernate.envers" +
            ".track_entities_changed_in_revision", "true");

        //add all of your entities
        config.addAnnotatedClass(ChargeCode.class);
        config.addAnnotatedClass(TrackedTask.class);
        config.addAnnotatedClass(User.class);
        config.addAnnotatedClass(TimeBlock.class);

        SchemaExport export = new EnversSchemaGenerator(config).export()
            .setDelimiter(";");
        export.setOutputFile("schema.sql");
        export.execute(true, false, false, false);
    }

}
