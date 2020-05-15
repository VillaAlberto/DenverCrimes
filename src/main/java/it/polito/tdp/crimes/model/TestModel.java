package it.polito.tdp.crimes.model;

public class TestModel {

	public static void main(String[] args) {
		Model model= new Model();
		model.creaGrafo(8, "murder");
		System.out.println(model.numVertex());
		System.out.println(model.numEdges());
		System.out.println(model.output());
	}

}
