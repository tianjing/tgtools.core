package tgtools.data;

import java.io.Serializable;

/**
 *
 * @author tianjing
 */
public class DataParameter
        implements Cloneable, Serializable {
    public static final String ELEMENT_PARAM = "DataParameter";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_DBTYPE = "dbType";
    public static final String ATTR_DIRECTION = "direction";
    public static final String ATTR_SIZE = "size";
    public static final String ELEMENT_VALUE = "Value";
    private static final long serialVersionUID = -3000305278740359158L;
    private String name;
    private Object value;
    private int dataType;
    private DataParameterDirection direction;
    private int size;

    public DataParameter() {
        this(null, 0);
    }

    public DataParameter(Object pValue, int pDataType) {
        setName(null);
        setValue(pValue);
        setDataType(pDataType);
        setDirection(DataParameterDirection.In);
        setSize(-1);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String pName) {
        this.name = pName;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object pValue) {
        this.value = pValue;
    }

    public int getDataType() {
        return this.dataType;
    }

    public void setDataType(int pDataType) {
        this.dataType = pDataType;
    }

    public DataParameterDirection getDirection() {
        return this.direction;
    }

    public void setDirection(DataParameterDirection pDirection) {
        this.direction = pDirection;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int pSize) {
        this.size = pSize;
    }

    @Override
    public DataParameter clone() {
        DataParameter result = new DataParameter();
        result.setDataType(getDataType());
        result.setDirection(getDirection());
        result.setName(getName());
        result.setSize(getSize());
        result.setValue(getValue());
        return result;
    }

}
