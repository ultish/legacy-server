package xw.legacyserver.configs;

import org.hibernate.cfg.EJB3NamingStrategy;
import org.hibernate.internal.util.StringHelper;

import java.util.regex.Pattern;

public class CamelCaseNamingStrategy extends EJB3NamingStrategy {

    public String logicalCollectionColumnName(
        String columnName,
        String propertyName,
        String referencedColumn
    ) {
        String name = columnName;
        if (name != null && name.length() != 0) {
            if (Pattern.compile("[A-Z]+").matcher(name).find()) {
                name = "\"" + name + "\"";
            }
        }
        return StringHelper.isNotEmpty(name) ?
            name :
            StringHelper.unqualify(propertyName) + "_" + referencedColumn;
    }

    @Override
    public String propertyToColumnName(String propertyName) {
        //        System.out.println("HUH " + propertyName);
        if (Pattern.compile("[A-Z]+").matcher(propertyName).find()) {
            return "\"" + propertyName + "\"";
        } else {
            return super.propertyToColumnName(propertyName);
        }
    }

    @Override
    public String joinKeyColumnName(
        String joinedColumn, String joinedTable
    ) {

        //        System.out.println("++++joinedColumn " + joinedColumn);
        //
        //        System.out.println("++++joinedTable " + joinedTable);
        //
        //        System.out.println("++++result " + super.joinKeyColumnName(
        //            joinedColumn,
        //            joinedTable
        //        ));
        return super.joinKeyColumnName(joinedColumn, joinedTable);
    }

    @Override
    public String foreignKeyColumnName(
        String propertyName,
        String propertyEntityName,
        String propertyTableName,
        String referencedColumnName
    ) {

        //        System.out.println("++++propertyName " + propertyName);
        //        System.out.println("++++propertyEntityName " +
        //        propertyEntityName);
        //        System.out.println("++++propertyTableName " +
        //        propertyTableName);
        //        System.out.println("++++referencedColumnName " +
        //        referencedColumnName);

        return super.foreignKeyColumnName(
            propertyName,
            propertyEntityName,
            propertyTableName,
            referencedColumnName
        );
    }
}
