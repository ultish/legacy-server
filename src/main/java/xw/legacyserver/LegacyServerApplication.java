package xw.legacyserver;

import org.hibernate.cfg.Configuration;
import org.hibernate.envers.tools.hbm2ddl.EnversSchemaGenerator;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import xw.legacyserver.entities.ChargeCode;
import xw.legacyserver.entities.TrackedTask;

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
        config.setProperty(
            "hibernate.dialect",
            "org.hibernate.dialect.PostgreSQL9Dialect"
        );
        config.setProperty("org.hibernate.envers.audit_table_prefix", "aud_");
        config.setProperty("org.hibernate.envers.audit_table_suffix", "");

        //add all of your entities
        config.addAnnotatedClass(ChargeCode.class);
        config.addAnnotatedClass(TrackedTask.class);

        SchemaExport export = new EnversSchemaGenerator(config).export()
            .setDelimiter(";");
        export.setOutputFile("schema.sql");
        export.execute(true, false, false, false);
    }

}
