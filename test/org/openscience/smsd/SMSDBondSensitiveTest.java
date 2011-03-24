/* Copyright (C) 2009-2010 Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.smsd;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.smsd.algorithm.vflib.Molecules;
import org.openscience.smsd.interfaces.Algorithm;

/**
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 * 
 * @cdk.module test-smsd
 * @cdk.require java1.6+
 */
public class SMSDBondSensitiveTest {

    private static IMolecule Napthalene;
    private static IMolecule Cyclohexane;
    private static IMolecule Benzene;

    @BeforeClass
    public static void setUp() throws CDKException {
        Napthalene = Molecules.createNaphthalene();
        Cyclohexane = Molecules.createCyclohexane();
        Benzene = Molecules.createBenzene();
    }

    @Test
    public void testSubgraph() throws Exception {

        Isomorphism sbf = new Isomorphism(Algorithm.DEFAULT_1, true);
        sbf.init(Benzene, Napthalene);
        sbf.setChemFilters(false, false, false);
        System.out.println("Match " + sbf.getTanimotoSimilarity());
        System.out.println("Match count: " + sbf.getAllAtomMapping().size());
        Assert.assertEquals(true, sbf.isSubgraph());
        Assert.assertEquals(24, sbf.getAllAtomMapping().size());
    }

    @Test
    public void testMatchCount() throws CDKException {
        Isomorphism smsd = new Isomorphism(Algorithm.VFLibMCS, true);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer query = sp.parseSmiles("CC");
        IAtomContainer target = sp.parseSmiles("C1CCC12CCCC2");

        smsd.init(query, target);
        boolean foundMatches = smsd.isSubgraph();
        Assert.assertEquals(18, smsd.getAllAtomMapping().size());
        Assert.assertTrue(foundMatches);

        IQueryAtomContainer queryContainer = QueryAtomContainerCreator.createSymbolAndBondOrderQueryContainer(query);
        smsd.init(queryContainer, target);
        foundMatches = smsd.isSubgraph();
        Assert.assertTrue(foundMatches);
    }

    @Test
    public void testVFLib() throws Exception {

        Isomorphism sbf = new Isomorphism(Algorithm.VFLibMCS, true);
        sbf.init(Benzene, Benzene);
        sbf.setChemFilters(true, true, true);
        Assert.assertEquals(true, sbf.isSubgraph());

    }

    @Test
    public void testSubStructure() throws Exception {
        Isomorphism sbf = new Isomorphism(Algorithm.DEFAULT_1, true);
        sbf.init(Benzene, Benzene);
        sbf.setChemFilters(false, false, false);
        Assert.assertEquals(true, sbf.isSubgraph());
    }

    @Test
    public void testCDKMCS() throws Exception {
        Isomorphism ebimcs = new Isomorphism(Algorithm.CDKMCS, true);
        ebimcs.init(Benzene, Benzene);
        ebimcs.setChemFilters(true, true, true);
        Assert.assertEquals(6, ebimcs.getFirstMapping().size());
        Assert.assertEquals(true, ebimcs.isSubgraph());
    }

    @Test
    public void testMCSPlus() throws Exception {

        Isomorphism ebimcs = new Isomorphism(Algorithm.MCSPlus, false);
        ebimcs.init(Cyclohexane, Benzene);
        ebimcs.setChemFilters(true, true, true);
        Assert.assertEquals(6, ebimcs.getFirstMapping().size());
        Assert.assertEquals(true, ebimcs.isSubgraph());

        ebimcs = new Isomorphism(Algorithm.CDKMCS, true);
        ebimcs.init(Cyclohexane, Benzene);
        ebimcs.setChemFilters(true, true, true);
        Assert.assertEquals(false, ebimcs.isSubgraph());
    }

    @Test
    public void testSMSD() throws Exception {


//        Isomorphism ebimcs = new Isomorphism(Algorithm.VFLibMCS, true);
//        ebimcs.init(Cyclohexane, Benzene, true, true);
//        ebimcs.setChemFilters(true, true, true);
//        Assert.assertEquals(1, ebimcs.getFirstMapping().size());

        Isomorphism ebimcs1 = new Isomorphism(Algorithm.DEFAULT, true);
        ebimcs1.init(Benzene, Napthalene);
        ebimcs1.setChemFilters(true, true, true);
        Assert.assertEquals(6, ebimcs1.getFirstAtomMapping().size());

        ebimcs1 = new Isomorphism(Algorithm.DEFAULT, false);
        ebimcs1.init(Benzene, Napthalene);
        ebimcs1.setChemFilters(true, true, true);
        Assert.assertEquals(6, ebimcs1.getFirstAtomMapping().size());

        ebimcs1 = new Isomorphism(Algorithm.VFLibMCS, true);
        ebimcs1.init(Benzene, Napthalene);
        ebimcs1.setChemFilters(true, true, true);
        Assert.assertEquals(6, ebimcs1.getFirstAtomMapping().size());

        ebimcs1 = new Isomorphism(Algorithm.CDKMCS, true);
        ebimcs1.init(Benzene, Napthalene);
        ebimcs1.setChemFilters(true, true, true);
        Assert.assertEquals(6, ebimcs1.getFirstAtomMapping().size());

        ebimcs1 = new Isomorphism(Algorithm.MCSPlus, true);
        ebimcs1.init(Benzene, Napthalene);
        ebimcs1.setChemFilters(true, true, true);
        Assert.assertEquals(6, ebimcs1.getFirstAtomMapping().size());
    }

    @Test
    public void testSMSDCyclohexaneBenzeneSubgraph() throws Exception {

//        IQueryAtomContainer queryContainer = QueryAtomContainerCreator.createSymbolAndBondOrderQueryContainer(Cyclohexane);

        Isomorphism ebimcs = new Isomorphism(Algorithm.VFLibMCS, true);
        ebimcs.init(Cyclohexane, Benzene);
        ebimcs.setChemFilters(true, true, true);
        Assert.assertEquals(false, ebimcs.isSubgraph());
    }

    @Test
    public void testSMSDBondSensitive() throws Exception {

        Isomorphism ebimcs3 = new Isomorphism(Algorithm.CDKMCS, true);
        ebimcs3.init(Cyclohexane, Benzene);
        ebimcs3.setChemFilters(false, false, false);
        Assert.assertEquals(false, ebimcs3.isSubgraph());

        Isomorphism ebimcs4 = new Isomorphism(Algorithm.CDKMCS, true);
        ebimcs4.init(Benzene, Napthalene);
        ebimcs4.setChemFilters(true, true, true);
        Assert.assertEquals(6, ebimcs4.getFirstAtomMapping().size());

        Isomorphism ebimcs5 = new Isomorphism(Algorithm.VFLibMCS, true);
        ebimcs5.init(Cyclohexane, Benzene);
        ebimcs5.setChemFilters(true, true, true);
        Assert.assertEquals(false, ebimcs5.isSubgraph());

        Isomorphism ebimcs6 = new Isomorphism(Algorithm.VFLibMCS, true);
        ebimcs6.init(Benzene, Napthalene);
        ebimcs6.setChemFilters(true, true, true);
        Assert.assertEquals(6, ebimcs6.getFirstAtomMapping().size());

        Isomorphism ebimcs7 = new Isomorphism(Algorithm.MCSPlus, true);
        ebimcs7.init(Cyclohexane, Benzene);
        ebimcs7.setChemFilters(true, true, true);
        Assert.assertFalse(ebimcs7.isSubgraph());

        Isomorphism ebimcs8 = new Isomorphism(Algorithm.MCSPlus, true);
        ebimcs8.init(Benzene, Napthalene);
        ebimcs8.setChemFilters(true, true, true);
        Assert.assertEquals(6, ebimcs8.getFirstAtomMapping().size());
    }

    @Test
    public void testSMSDChemicalFilters() throws Exception {

        Isomorphism ebimcs1 = new Isomorphism(Algorithm.DEFAULT, true);
        ebimcs1.init(Napthalene, Benzene);
        ebimcs1.setChemFilters(true, true, true);
        Assert.assertEquals(6, ebimcs1.getAllMapping().size());
        Assert.assertEquals(false, ebimcs1.isSubgraph());
    }

//    @Test
//    public void testSingleMappingTesting() throws Exception {
//
//        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
//        IAtomContainer atomContainer = sp.parseSmiles("C");
//        IQueryAtomContainer query = QueryAtomContainerCreator.createBasicQueryContainer(atomContainer);
//
//        IMolecule mol2 = Molecules.create4Toluene();
//
//        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);
//        CDKHueckelAromaticityDetector.detectAromaticity(mol2);
//
//        boolean bondSensitive = true;
//        boolean removeHydrogen = true;
//        boolean stereoMatch = true;
//        boolean fragmentMinimization = true;
//        boolean energyMinimization = true;
//
//        Isomorphism comparison1 = new Isomorphism(Algorithm.DEFAULT, bondSensitive);
//        comparison1.init(query, mol2, removeHydrogen, true);
//        comparison1.setChemFilters(stereoMatch, fragmentMinimization, energyMinimization);
//
//        Assert.assertEquals(true, comparison1.isSubgraph());
//        Assert.assertEquals(1, comparison1.getAllMapping().size());
//
//
//    }
    /**
     * frag is a subgraph of the het mol
     * @throws Exception
     */
    @Test
    public void testSMSDAdpAtpSubgraph() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String adp = "NC1=NC=NC2=C1N=CN2[C@@H]1O[C@H](COP(O)(=O)OP(O)(O)=O)[C@@H](O)[C@H]1O";
        String atp = "NC1=NC=NC2=C1N=CN2[C@@H]1O[C@H](COP(O)(=O)OP(O)(=O)OP(O)(O)=O)[C@@H](O)[C@H]1O";
        IAtomContainer mol1 = sp.parseSmiles(adp);
        IAtomContainer mol2 = sp.parseSmiles(atp);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);

//	Calling the main algorithm to perform MCS cearch

        CDKHueckelAromaticityDetector.detectAromaticity(mol1);
        CDKHueckelAromaticityDetector.detectAromaticity(mol2);

        mol1=AtomContainerManipulator.removeHydrogens(mol1);
        mol2=AtomContainerManipulator.removeHydrogens(mol2);
        
        boolean bondSensitive = true;
        boolean removeHydrogen = true;
        boolean stereoMatch = true;
        boolean fragmentMinimization = true;
        boolean energyMinimization = true;

        Isomorphism comparison = new Isomorphism(Algorithm.DEFAULT, bondSensitive);
        comparison.init(mol1, mol2);
        comparison.setChemFilters(stereoMatch, fragmentMinimization, energyMinimization);

//      Get modified Query and Target Molecules as Mappings will correspond to these molecules
        Assert.assertEquals(true, comparison.isSubgraph());
        Assert.assertEquals(2, comparison.getAllMapping().size());
        Assert.assertEquals(27, comparison.getFirstMapping().size());


    }

    @Test
    public void testSMSDLargeSubgraph() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String c03374 = "CC1=C(C=C)\\C(NC1=O)=C"
                + "\\C1=C(C)C(CCC(=O)O[C@@H]2O[C@@H]"
                + "([C@@H](O)[C@H](O)[C@H]2O)C(O)=O)"
                + "=C(CC2=C(CCC(O)=O)C(C)=C(N2)"
                + "\\C=C2NC(=O)C(C=C)=C/2C)N1";

        String c05787 = "CC1=C(C=C)\\C(NC1=O)=C"
                + "\\C1=C(C)C(CCC(=O)O[C@@H]2O[C@@H]"
                + "([C@@H](O)[C@H](O)[C@H]2O)C(O)=O)"
                + "=C(CC2=C(CCC(=O)O[C@@H]3O[C@@H]"
                + "([C@@H](O)[C@H](O)[C@H]3O)C(O)=O)"
                + "C(C)=C(N2)"
                + "\\C=C2NC(=O)C(C=C)=C/2C)N1";

        IAtomContainer mol1 = sp.parseSmiles(c03374);
        IAtomContainer mol2 = sp.parseSmiles(c05787);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);

        IAtomContainer source = AtomContainerManipulator.removeHydrogens(mol1);
        IAtomContainer target = AtomContainerManipulator.removeHydrogens(mol2);

//	Calling the main algorithm to perform MCS cearch

        CDKHueckelAromaticityDetector.detectAromaticity(source);
        CDKHueckelAromaticityDetector.detectAromaticity(target);

        boolean bondSensitive = true;
        boolean removeHydrogen = true;
        boolean stereoMatch = true;
        boolean fragmentMinimization = true;
        boolean energyMinimization = true;

        Isomorphism comparison = new Isomorphism(Algorithm.DEFAULT_1, bondSensitive);
        comparison.init(source, target);
        comparison.setChemFilters(stereoMatch, fragmentMinimization, energyMinimization);

        Assert.assertEquals(true, comparison.isSubgraph());
        Assert.assertEquals(55, comparison.getFirstMapping().size());


    }
}
