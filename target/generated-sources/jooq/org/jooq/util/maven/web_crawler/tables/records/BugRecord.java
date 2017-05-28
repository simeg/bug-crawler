/*
 * This file is generated by jOOQ.
*/
package org.jooq.util.maven.web_crawler.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.util.maven.web_crawler.tables.Bug;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BugRecord extends UpdatableRecordImpl<BugRecord> implements Record6<Integer, String, String, String, String, Timestamp> {

    private static final long serialVersionUID = 145631609;

    /**
     * Setter for <code>public.bug.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.bug.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.bug.type</code>.
     */
    public void setType(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.bug.type</code>.
     */
    public String getType() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.bug.base_url</code>.
     */
    public void setBaseUrl(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.bug.base_url</code>.
     */
    public String getBaseUrl() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.bug.path</code>.
     */
    public void setPath(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.bug.path</code>.
     */
    public String getPath() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.bug.description</code>.
     */
    public void setDescription(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.bug.description</code>.
     */
    public String getDescription() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.bug.time_inserted</code>.
     */
    public void setTimeInserted(Timestamp value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.bug.time_inserted</code>.
     */
    public Timestamp getTimeInserted() {
        return (Timestamp) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<Integer, String, String, String, String, Timestamp> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<Integer, String, String, String, String, Timestamp> valuesRow() {
        return (Row6) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Bug.BUG.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Bug.BUG.TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Bug.BUG.BASE_URL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Bug.BUG.PATH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return Bug.BUG.DESCRIPTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field6() {
        return Bug.BUG.TIME_INSERTED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getBaseUrl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value6() {
        return getTimeInserted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BugRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BugRecord value2(String value) {
        setType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BugRecord value3(String value) {
        setBaseUrl(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BugRecord value4(String value) {
        setPath(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BugRecord value5(String value) {
        setDescription(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BugRecord value6(Timestamp value) {
        setTimeInserted(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BugRecord values(Integer value1, String value2, String value3, String value4, String value5, Timestamp value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BugRecord
     */
    public BugRecord() {
        super(Bug.BUG);
    }

    /**
     * Create a detached, initialised BugRecord
     */
    public BugRecord(Integer id, String type, String baseUrl, String path, String description, Timestamp timeInserted) {
        super(Bug.BUG);

        set(0, id);
        set(1, type);
        set(2, baseUrl);
        set(3, path);
        set(4, description);
        set(5, timeInserted);
    }
}
