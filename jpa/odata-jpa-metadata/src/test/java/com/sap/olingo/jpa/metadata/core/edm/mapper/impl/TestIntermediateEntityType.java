package com.sap.olingo.jpa.metadata.core.edm.mapper.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.metamodel.EntityType;

import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlCollection;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression.ConstantExpressionType;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlExpression;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.reflections.Reflections;

import com.sap.olingo.jpa.metadata.api.JPAEdmMetadataPostProcessor;
import com.sap.olingo.jpa.metadata.core.edm.annotation.EdmEnumeration;
import com.sap.olingo.jpa.metadata.core.edm.mapper.api.JPAOnConditionItem;
import com.sap.olingo.jpa.metadata.core.edm.mapper.api.JPAPath;
import com.sap.olingo.jpa.metadata.core.edm.mapper.api.JPAStructuredType;
import com.sap.olingo.jpa.metadata.core.edm.mapper.exception.ODataJPAModelException;
import com.sap.olingo.jpa.metadata.core.edm.mapper.extention.IntermediateEntityTypeAccess;
import com.sap.olingo.jpa.metadata.core.edm.mapper.extention.IntermediateNavigationPropertyAccess;
import com.sap.olingo.jpa.metadata.core.edm.mapper.extention.IntermediatePropertyAccess;
import com.sap.olingo.jpa.metadata.core.edm.mapper.extention.IntermediateReferenceList;
import com.sap.olingo.jpa.processor.core.testmodel.ABCClassifiaction;
import com.sap.olingo.jpa.processor.core.testmodel.TestDataConstants;

public class TestIntermediateEntityType extends TestMappingRoot {
  private Set<EntityType<?>> etList;
  private IntermediateSchema schema;

  @Before
  public void setup() throws ODataJPAModelException {
    IntermediateModelElement.setPostProcessor(new DefaultEdmPostProcessor());
    final Reflections r = mock(Reflections.class);
    when(r.getTypesAnnotatedWith(EdmEnumeration.class)).thenReturn(new HashSet<>(Arrays.asList(new Class<?>[] {
        ABCClassifiaction.class })));

    etList = emf.getMetamodel().getEntities();
    schema = new IntermediateSchema(new JPAEdmNameBuilder(PUNIT_NAME), emf.getMetamodel(), r);
  }

  @Test
  public void checkEntityTypeCanBeCreated() throws ODataJPAModelException {

    new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType("BusinessPartner"), schema);
  }

  @Test
  public void checkEntityTypeIgnoreSet() throws ODataJPAModelException {

    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "DummyToBeIgnored"), schema);
    et.getEdmItem();
    assertTrue(et.ignore());
  }

  @Test
  public void checkGetAllProperties() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "BusinessPartner"), schema);
    assertEquals("Wrong number of entities", TestDataConstants.NO_DEC_ATTRIBUTES_BUISNESS_PARTNER, et.getEdmItem()
        .getProperties()
        .size());
  }

  @Test
  public void checkGetPropertyByNameNotNull() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "BusinessPartner"), schema);
    assertNotNull(et.getEdmItem().getProperty("Type"));
  }

  @Test
  public void checkGetPropertyByNameCorrectEntity() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "BusinessPartner"), schema);
    assertEquals("Type", et.getEdmItem().getProperty("Type").getName());
  }

  @Test
  public void checkGetPropertyByNameCorrectEntityID() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "BusinessPartner"), schema);
    assertEquals("ID", et.getEdmItem().getProperty("ID").getName());
  }

  @Test
  public void checkGetPathByNameCorrectEntityID() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "BusinessPartner"), schema);
    assertEquals("ID", et.getPath("ID").getLeaf().getExternalName());
  }

  @Test
  public void checkGetPathByNameIgnore() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "BusinessPartner"), schema);
    assertNull(et.getPath("CustomString2"));
  }

  @Test
  public void checkGetPathByNameIgnoreCompexType() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "BusinessPartner"), schema);
    assertNull(et.getPath("Address/RegionCodePublisher"));
  }

  @Test
  public void checkGetInheritedAttributeByNameCorrectEntityID() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "Person"), schema);
    assertEquals("ID", et.getPath("ID").getLeaf().getExternalName());
  }

  @Test
  public void checkGetAllNaviProperties() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "BusinessPartner"), schema);
    assertEquals("Wrong number of entities", 1, et.getEdmItem().getNavigationProperties().size());
  }

  @Test
  public void checkGetNaviPropertyByNameNotNull() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "BusinessPartner"), schema);
    assertNotNull(et.getEdmItem().getNavigationProperty("Roles"));
  }

  @Test
  public void checkGetNaviPropertyByNameCorrectEntity() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "BusinessPartner"), schema);
    assertEquals("Roles", et.getEdmItem().getNavigationProperty("Roles").getName());
  }

  @Test
  public void checkGetAssoziationOfComplexTypeByNameCorrectEntity() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "BusinessPartner"), schema);
    assertEquals("Address/AdministrativeDivision", et.getAssociationPath("Address/AdministrativeDivision").getAlias());
  }

  @Test
  public void checkGetAssoziationOfComplexTypeByNameJoinColumns() throws ODataJPAModelException {
    int actCount = 0;
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "BusinessPartner"), schema);
    for (JPAOnConditionItem item : et.getAssociationPath("Address/AdministrativeDivision").getJoinColumnsList()) {
      if (item.getLeftPath().getAlias().equals("Address/Region")) {
        assertTrue(item.getRightPath().getAlias().equals("DivisionCode"));
        actCount++;
      }
      if (item.getLeftPath().getAlias().equals("Address/RegionCodeID")) {
        assertTrue(item.getRightPath().getAlias().equals("CodeID"));
        actCount++;
      }
      if (item.getLeftPath().getAlias().equals("Address/RegionCodePublisher")) {
        assertTrue(item.getRightPath().getAlias().equals("CodePublisher"));
        actCount++;
      }
    }
    assertEquals("Not all join columns found", 3, actCount);
  }

  @Test
  public void checkGetPropertiesSkipIgnored() throws ODataJPAModelException {
    PostProcessorSetIgnore pPDouble = new PostProcessorSetIgnore();
    IntermediateModelElement.setPostProcessor(pPDouble);

    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "BusinessPartner"), schema);
    assertEquals("Wrong number of entities", TestDataConstants.NO_DEC_ATTRIBUTES_BUISNESS_PARTNER - 1, et.getEdmItem()
        .getProperties().size());
  }

  @Test
  public void checkGetIsAbstract() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "BusinessPartner"), schema);
    assertTrue(et.getEdmItem().isAbstract());
  }

  @Test
  public void checkGetIsNotAbstract() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "Organization"), schema);
    assertFalse(et.getEdmItem().isAbstract());
  }

  @Test
  public void checkGetHasBaseType() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "Organization"), schema);
    assertEquals(PUNIT_NAME + ".BusinessPartner", et.getEdmItem().getBaseType());
  }

  @Test
  public void checkGetKeyProperties() throws ODataJPAModelException {
    IntermediateEntityType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "BusinessPartnerRole"), schema);
    assertEquals("Wrong number of key propeties", 2, et.getEdmItem().getKey().size());
  }

  @Test
  public void checkGetAllAttributes() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "BusinessPartnerRole"), schema);
    assertEquals("Wrong number of entities", 2, et.getPathList().size());
  }

  @Test
  public void checkGetAllAttributesWithBaseType() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "Organization"), schema);
    int exp = TestDataConstants.NO_ATTRIBUTES_BUISNESS_PARTNER
        + TestDataConstants.NO_ATTRIBUTES_POSTAL_ADDRESS
        + TestDataConstants.NO_ATTRIBUTES_COMMUNICATION_DATA
        + 2 * TestDataConstants.NO_ATTRIBUTES_CHANGE_INFO
        + TestDataConstants.NO_ATTRIBUTES_ORGANIZATION;
    assertEquals("Wrong number of entities", exp, et.getPathList().size());
  }

  @Test
  public void checkGetAllAttributesWithBaseTypeFields() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "Organization"), schema);

    assertNotNull(et.getPath("Type"));
    assertNotNull(et.getPath("Name1"));
    assertNotNull(et.getPath("Address" + JPAPath.PATH_SEPERATOR + "Region"));
    assertNotNull(et.getPath("AdministrativeInformation" + JPAPath.PATH_SEPERATOR
        + "Created" + JPAPath.PATH_SEPERATOR + "By"));
  }

  @Test
  public void checkGetAllAttributeIDWithBaseType() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "Organization"), schema);
    assertEquals("ID", et.getPath("ID").getAlias());
  }

  @Test
  public void checkGetKeyWithBaseType() throws ODataJPAModelException {
    IntermediateEntityType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "Organization"), schema);
    assertEquals(1, et.getKey().size());
  }

  @Test
  public void checkEmbeddedIdResovedProperties() throws ODataJPAModelException {
    IntermediateEntityType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "AdministrativeDivisionDescription"), schema);
    assertEquals(5, et.getEdmItem().getProperties().size());
  }

  @Test
  public void checkEmbeddedIdResovedKey() throws ODataJPAModelException {
    IntermediateEntityType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "AdministrativeDivisionDescription"), schema);
    assertEquals(4, et.getEdmItem().getKey().size());
  }

  @Test
  public void checkEmbeddedIdResovedKeyInternal() throws ODataJPAModelException {
    IntermediateEntityType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "AdministrativeDivisionDescription"), schema);
    assertEquals(4, et.getKey().size());
  }

  @Test
  public void checkEmbeddedIdResovedPath() throws ODataJPAModelException {
    JPAStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "AdministrativeDivisionDescription"), schema);
    assertEquals(5, et.getPathList().size());
  }

  @Test
  public void checkEmbeddedIdResovedPathCodeId() throws ODataJPAModelException {
    JPAStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "AdministrativeDivisionDescription"), schema);
    assertEquals(2, et.getPath("CodeID").getPath().size());
  }

  @Test
  public void checkHasStreamNoProperties() throws ODataJPAModelException {
    IntermediateEntityType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "PersonImage"), schema);
    assertEquals(2, et.getEdmItem().getProperties().size());
  }

  @Test
  public void checkHasStreamTrue() throws ODataJPAModelException {
    IntermediateEntityType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "PersonImage"), schema);
    assertTrue(et.getEdmItem().hasStream());
  }

  @Test
  public void checkHasStreamFalse() throws ODataJPAModelException {
    IntermediateEntityType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "BusinessPartner"), schema);
    assertFalse(et.getEdmItem().hasStream());
  }

  @Test
  public void checkHasETagTrue() throws ODataJPAModelException {
    IntermediateEntityType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "BusinessPartner"), schema);
    assertTrue(et.hasEtag());
  }

  @Test
  public void checkHasETagTrueIfInherited() throws ODataJPAModelException {
    IntermediateEntityType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "Organization"), schema);
    assertTrue(et.hasEtag());
  }

  @Test
  public void checkHasETagFalse() throws ODataJPAModelException {
    IntermediateEntityType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "AdministrativeDivision"), schema);
    assertFalse(et.hasEtag());
  }

  @Test
  public void checkIgnoreIfAsEntitySet() throws ODataJPAModelException {
    IntermediateEntityType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "BestOrganization"), schema);
    assertTrue(et.ignore());
  }

  @Test
  public void checkAnnotationSet() throws ODataJPAModelException {
    IntermediateModelElement.setPostProcessor(new PostProcessorSetIgnore());
    IntermediateEntityType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "PersonImage"), schema);
    List<CsdlAnnotation> act = et.getEdmItem().getAnnotations();
    assertEquals(1, act.size());
    assertEquals("Core.AcceptableMediaTypes", act.get(0).getTerm());
  }

  @Test
  public void checkGetProptertyByDBFieldName() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "BusinessPartner"), schema);
    assertEquals("Type", et.getPropertyByDBField("\"Type\"").getExternalName());
  }

  @Test
  public void checkGetProptertyByDBFieldNameFromSuperType() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "Organization"), schema);
    assertEquals("Type", et.getPropertyByDBField("\"Type\"").getExternalName());
  }

  @Test
  public void checkGetProptertyByDBFieldNameFromEmbedded() throws ODataJPAModelException {
    IntermediateStructuredType et = new IntermediateEntityType(new JPAEdmNameBuilder(PUNIT_NAME), getEntityType(
        "AdministrativeDivisionDescription"), schema);
    assertEquals("CodeID", et.getPropertyByDBField("\"CodeID\"").getExternalName());
  }

  @Ignore
  @Test
  public void checkGetPropertyWithEnumerationType() {

  }

  private class PostProcessorSetIgnore extends JPAEdmMetadataPostProcessor {

    @Override
    public void processProperty(IntermediatePropertyAccess property, String jpaManagedTypeClassName) {
      if (jpaManagedTypeClassName.equals(
          "com.sap.olingo.jpa.processor.core.testmodel.BusinessPartner")) {
        if (property.getInternalName().equals("communicationData")) {
          property.setIgnore(true);
        }
      }
    }

    @Override
    public void processNavigationProperty(IntermediateNavigationPropertyAccess property,
        String jpaManagedTypeClassName) {}

    @Override
    public void processEntityType(IntermediateEntityTypeAccess entity) {
      if (entity.getExternalName().equals("PersonImage")) {
        List<CsdlExpression> items = new ArrayList<>();
        CsdlCollection exp = new CsdlCollection();
        exp.setItems(items);
        CsdlConstantExpression mimeType = new CsdlConstantExpression(ConstantExpressionType.String, "ogg");
        items.add(mimeType);
        CsdlAnnotation annotation = new CsdlAnnotation();
        annotation.setExpression(exp);
        annotation.setTerm("Core.AcceptableMediaTypes");
        List<CsdlAnnotation> annotations = new ArrayList<>();
        annotations.add(annotation);
        entity.addAnnotations(annotations);
      }
    }

    @Override
    public void provideReferences(IntermediateReferenceList references) throws ODataJPAModelException {}
  }

  private EntityType<?> getEntityType(String typeName) {
    for (EntityType<?> entityType : etList) {
      if (entityType.getJavaType().getSimpleName().equals(typeName)) {
        return entityType;
      }
    }
    return null;
  }
}
