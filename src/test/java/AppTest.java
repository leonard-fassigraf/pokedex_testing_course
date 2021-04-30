package test.java;

import org.fluentlenium.adapter.FluentTest;
import org.junit.ClassRule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import static org.fluentlenium.core.filter.FilterConstructor.*;
import org.sql2o.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

import main.java.DB;
import main.java.App;
import spark.Spark;

public class AppTest extends FluentTest {
  public WebDriver webDriver = new HtmlUnitDriver();

  @Before
  public void beforeEach() {
    DB.sql2o = new Sql2o("jdbc:postgresql://localhost:5432/pokedex_test", "postgres", "yamizi");
  }

  @After
  public void afterEach() {
    try(Connection con = DB.sql2o.open()) {
      String deletePokemonsQuery = "DELETE FROM pokemons *;";
      String deleteMovesQuery = "DELETE FROM moves *;";
      String deleteMovesPokemonsQuery = "DELETE FROM moves_pokemons *;";
      con.createQuery(deletePokemonsQuery).executeUpdate();
      con.createQuery(deleteMovesQuery).executeUpdate();
      con.createQuery(deleteMovesPokemonsQuery).executeUpdate();
    }
  }

  @Override
  public WebDriver getDefaultDriver() {
    return webDriver;
  }

  @BeforeClass
  public static void beforeAll() {
    String[] args = {};
    App.main(args);
  }

  @AfterClass
  public static void afterAll() {
    Spark.stop();
  }

  @Test
  public void acceptance_test_check_if_app_is_in_root_page() {
    goTo("http://localhost:4567/");
    assertThat(pageSource()).contains("Pokedex");
  }

  @Test
  public void acceptance_test_check_if_ivysaur_and_charizard_are_in_the_deck() {
    goTo("http://localhost:4567/");
    click("#viewDex");
    assertThat(pageSource().contains("Ivysaur"));
    assertThat(pageSource().contains("Charizard"));
  }

  @Test
  public void acceptance_test_check_if_charizard_page_is_displayed() {
    goTo("http://localhost:4567/pokepage/6");
    assertThat(pageSource().contains("Charizard"));
  }

  @Test
  public void acceptance_test_check_if_right_arrow_to_go_to_next_pokemon_page_is_working() {
    goTo("http://localhost:4567/pokepage/6");
    click(".glyphicon-triangle-right");
    assertThat(pageSource().contains("Squirtle"));
  }

  @Test
  public void acceptance_test_check_if_search_pokemon_by_name_is_working_when_some_matches_are_found() {
    goTo("http://localhost:4567/pokedex");
    fill("#name").with("char");
    assertThat(pageSource().contains("Charizard"));
  }

  @Test
  public void acceptance_test_check_if_search_pokemon_by_name_is_working_when_no_matches_are_found() {
    goTo("http://localhost:4567/pokedex");
    fill("#name").with("x");
    assertThat(pageSource().contains("No matches for your search results"));
  }
}