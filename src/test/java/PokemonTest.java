package test.java;

import static org.junit.Assert.*;

import org.junit.*;
import org.sql2o.*;

import main.java.Move;
import main.java.Pokemon;

import java.util.Arrays;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PokemonTest {
  private String attack;
  private String type;
  private double power;
  private int accuracy;
  private int expectedResult;

  @Rule
  public DatabaseRule database = new DatabaseRule();

  public PokemonTest(String attack, String type, double power, int accuracy, int expectedResult) {
    this.attack = attack;
    this.type = type;
    this.power = power;
    this.accuracy = accuracy;
    this.expectedResult = expectedResult;
  }

  @Test
  public void unit_test_check_if_new_pokemon_is_instantiated_correctly() {
    Pokemon myPokemon = new Pokemon("Squirtle", "Water", "None", "A cute turtle", 50.0, 12, 16, false);
    assertEquals(true, myPokemon instanceof Pokemon);
  }

  @Test
  public void unit_test_check_if_new_instantiated_pokemon_has_a_name_value() {
    Pokemon myPokemon = new Pokemon("Squirtle", "Water", "None", "A cute turtle", 50.0, 12, 16, false);
    assertEquals("Squirtle", myPokemon.getName());
  }

  @Test
  public void unit_test_check_if_list_of_pokemon_is_empty_at_first() {
    assertEquals(Pokemon.all().size(), 0);
  }

  @Test
  public void integration_test_check_if_two_pokemon_are_the_same() {
    Pokemon firstPokemon = new Pokemon("Squirtle", "Water", "None", "A cute turtle", 50.0, 12, 16, false);
    Pokemon secondPokemon = new Pokemon("Squirtle", "Water", "None", "A cute turtle", 50.0, 12, 16, false);
    assertTrue(firstPokemon.equals(secondPokemon));
  }

  @Test
  public void integration_test_check_if_new_pokemon_is_saved_correctly() {
    Pokemon newPokemon = new Pokemon("Squirtle", "Water", "None", "A cute turtle", 50.0, 12, 16, false);
    newPokemon.save();
    assertEquals(1, Pokemon.all().size());
  }

  @Test
  public void integration_test_check_if_a_pokemon_is_in_the_database() {
    Pokemon myPokemon = new Pokemon("Squirtle", "Water", "None", "A cute turtle", 50.0, 12, 16, false);
    myPokemon.save();
    Pokemon savedPokemon = Pokemon.find(myPokemon.getId());
    assertTrue(myPokemon.equals(savedPokemon));
  }

  @Test
  public void integration_test_check_if_adding_a_new_move_to_a_pokemon_is_working() {
    Move myMove = new Move("Punch", "Normal", 50.0, 100);
    myMove.save();
    Pokemon myPokemon = new Pokemon("Squirtle", "Water", "None", "A cute turtle", 50.0, 12, 16, false);
    myPokemon.save();
    myPokemon.addMove(myMove);
    Move savedMove = myPokemon.getMoves().get(0);
    assertTrue(myMove.equals(savedMove));
  }

  @Test
  public void integration_test_check_if_a_pokemon_is_deleted_correctly_and_that_also_associated_moves_are_deleted() {
    Pokemon myPokemon = new Pokemon("Squirtle", "Water", "None", "A cute turtle", 50.0, 12, 16, false);
    myPokemon.save();
    Move myMove = new Move("Bubble", "Water", 50.0, 100);
    myMove.save();
    myPokemon.addMove(myMove);
    myPokemon.delete();
    assertEquals(0, Pokemon.all().size());
    assertEquals(0, myPokemon.getMoves().size());
  }

  @Test
  public void integration_test_check_if_search_pokemon_by_name_is_working_if_the_input_is_a_string() {
    Pokemon myPokemon = new Pokemon("Squirtle", "Water", "None", "A cute turtle", 50.0, 12, 16, false);
    myPokemon.save();
    assertEquals(myPokemon, Pokemon.searchByName("squir").get(0));
  }

  @Parameters
  public static Collection<Object[]> values() {
    return Arrays.asList(new Object[][] {
      { "Atk1", "Fire", 50.0, 100, 400 }, //
      { "Atk2", "Thunder", 50.0, 100, 300 }, //
      { "Atk3", "Water", 20.0, 100, 460 }, //
    });
  }

  @Test
  public void integration_test_check_if_a_move_is_doing_the_right_amount_of_damage_to_the_targeted_pokemon() {
    Pokemon myPokemon = new Pokemon("Squirtle", "Water", "Normal", "A cute turtle", 50.0, 12, 16, false);
    myPokemon.save();
    myPokemon.hp = 500;
    Move myMove = new Move(attack, type, power, accuracy);
    myMove.attack(myPokemon);
    System.out.println(myPokemon.hp);
    myMove.attack(myPokemon);
        System.out.println(myPokemon.hp);
    myMove.attack(myPokemon);
        System.out.println(myPokemon.hp);
    myMove.attack(myPokemon);
    assertEquals(expectedResult, myPokemon.hp);
  }
}