package com.neptune.api.requestlater;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DataExtractorTest extends Mockito {

    Map<String, List<String>> result = new HashMap<>();
    public Map<String, String> rules = new HashMap<>();

    public static Map<String, List<String>> EXPECTED0 = new HashMap<>();
    public static String CASE0 = "<html>                                      "
            + "              <body>                                           "
            + "                <p>12345</p>                                   "
            + "                <p>var2='abcd'</p>                             "
            + "              </body>                                          "
            + "            </html>                                            ";

    public static Map<String, List<String>> EXPECTED1 = new HashMap<>();
    public static String CASE1 = "<html>                                      "
            + "<head>                                                         "
            + "</head>                                                        "
            + "<body>                                                         "
            + "  <table width=\"93%\" border=\"0\" cellpadding=\"0\"          "
            + "    cellspacing=\"0\" class=\"fundo-table2\">                  "
            + "                                                               "
            + "    <tbody><tr>                                                "
            + "      <td width=\"5%\" class=\"td_left\">&nbsp;</td>           "
            + "      <td width=\"31%\" height=\"18\" class=\"td_left\">01     "
            + "        de                                                     "
            + "        janeiro  </td>                                         "
            + "      <td width=\"18%\" class=\"td_left\">(                    "
            + "          Sexta                                                "
            + "        )                                                      "
            + "        </td>                                                  "
            + "      <td width=\"46%\" class=\"td_left\">                     "
            + "        Dia Mundial da Paz                                     "
            + "      </td>                                                    "
            + "    </tr>                                                      "
            + "                                                               "
            + "    <tr>                                                       "
            + "      <td width=\"5%\" class=\"td_left\">&nbsp;</td>           "
            + "      <td width=\"31%\" height=\"18\" class=\"td_left\">08     "
            + "        de                                                     "
            + "        fevereiro</td>                                         "
            + "      <td width=\"18%\" class=\"td_left\">(                    "
            + "          Segunda                                              "
            + "        )                                                      "
            + "        </td>                                                  "
            + "      <td width=\"46%\" class=\"td_left\">Carnaval</td>        "
            + "    </tr>                                                      "
            + "                                                               "
            + "    <tr>                                                       "
            + "      <td width=\"5%\" class=\"td_left\">&nbsp;</td>           "
            + "      <td width=\"31%\" height=\"18\" class=\"td_left\">09     "
            + "        de                                                     "
            + "        fevereiro</td>                                         "
            + "      <td width=\"18%\" class=\"td_left\">(                    "
            + "          Terça                                                "
            + "        )                                                      "
            + "        </td>                                                  "
            + "      <td width=\"46%\" class=\"td_left\">Carnaval</td>        "
            + "    </tr>                                                      "
            + "                                                               "
            + "    <tr>                                                       "
            + "      <td width=\"5%\" class=\"td_left\">&nbsp;</td>           "
            + "      <td width=\"31%\" height=\"18\" class=\"td_left\">25     "
            + "        de                                                     "
            + "        março    </td>                                         "
            + "      <td width=\"18%\" class=\"td_left\">(                    "
            + "          Sexta                                                "
            + "        )                                                      "
            + "        </td>                                                  "
            + "      <td width=\"46%\" class=\"td_left\">                     "
            + "        Sexta-Feira da Paixão                                  "
            + "      </td>                                                    "
            + "    </tr>                                                      "
            + "                                                               "
            + "    <tr>                                                       "
            + "      <td width=\"5%\" class=\"td_left\">&nbsp;</td>           "
            + "      <td width=\"31%\" height=\"18\" class=\"td_left\">21     "
            + "        de                                                     "
            + "        abril    </td>                                         "
            + "      <td width=\"18%\" class=\"td_left\">(                    "
            + "          Quinta                                               "
            + "        )                                                      "
            + "        </td>                                                  "
            + "      <td width=\"46%\" class=\"td_left\">Tiradentes</td>      "
            + "    </tr>                                                      "
            + "                                                               "
            + "    <tr>                                                       "
            + "      <td width=\"5%\" class=\"td_left\">&nbsp;</td>           "
            + "      <td width=\"31%\" height=\"18\" class=\"td_left\">01     "
            + "        de                                                     "
            + "        maio     </td>                                         "
            + "      <td width=\"18%\" class=\"td_left\">(                    "
            + "          Domingo                                              "
            + "        )                                                      "
            + "        </td>                                                  "
            + "      <td width=\"46%\" class=\"td_left\">Dia do Trabalho</td> "
            + "    </tr>                                                      "
            + "                                                               "
            + "    <tr>                                                       "
            + "      <td width=\"5%\" class=\"td_left\">&nbsp;</td>           "
            + "      <td width=\"31%\" height=\"18\" class=\"td_left\">26     "
            + "        de                                                     "
            + "        junho    </td>                                         "
            + "      <td width=\"18%\" class=\"td_left\">(                    "
            + "          Domingo                                              "
            + "        )                                                      "
            + "        </td>                                                  "
            + "      <td width=\"46%\" class=\"td_left\">Corpus Christi</td>  "
            + "    </tr>                                                      "
            + "                                                               "
            + "    <tr>                                                       "
            + "      <td width=\"5%\" class=\"td_left\">&nbsp;</td>           "
            + "      <td width=\"31%\" height=\"18\" class=\"td_left\">07     "
            + "        de                                                     "
            + "        setembro </td>                                         "
            + "      <td width=\"18%\" class=\"td_left\">(                    "
            + "          Quarta                                               "
            + "        )                                                      "
            + "        </td>                                                  "
            + "      <td width=\"46%\" class=\"td_left\">                     "
            + "        Independência do Brasil                                "
            + "      </td>                                                    "
            + "    </tr>                                                      "
            + "                                                               "
            + "    <tr>                                                       "
            + "      <td width=\"5%\" class=\"td_left\">&nbsp;</td>           "
            + "      <td width=\"31%\" height=\"18\" class=\"td_left\">12     "
            + "        de                                                     "
            + "        outubro  </td>                                         "
            + "      <td width=\"18%\" class=\"td_left\">(                    "
            + "          Quarta                                               "
            + "        )                                                      "
            + "        </td>                                                  "
            + "      <td width=\"46%\" class=\"td_left\">                     "
            + "        Nossa Srª Aparecida                                    "
            + "	     </td>                                                    "
            + "    </tr>                                                      "
            + "                                                               "
            + "    <tr>                                                       "
            + "      <td width=\"5%\" class=\"td_left\">&nbsp;</td>           "
            + "      <td width=\"31%\" height=\"18\" class=\"td_left\">02     "
            + "        de                                                     "
            + "        novembro </td>                                         "
            + "      <td width=\"18%\" class=\"td_left\">(                    "
            + "          Quarta                                               "
            + "        )                                                      "
            + "        </td>                                                  "
            + "      <td width=\"46%\" class=\"td_left\">Finados</td>         "
            + "    </tr>                                                      "
            + "                                                               "
            + "    <tr>                                                       "
            + "      <td width=\"5%\" class=\"td_left\">&nbsp;</td>           "
            + "      <td width=\"31%\" height=\"18\" class=\"td_left\">15     "
            + "        de                                                     "
            + "        novembro </td>                                         "
            + "      <td width=\"18%\" class=\"td_left\">(                    "
            + "          Terça                                                "
            + "        )                                                      "
            + "        </td>                                                  "
            + "      <td width=\"46%\" class=\"td_left\">                     "
            + "        Proclamação da República                               "
            + "	     </td>                                                    "
            + "    </tr>                                                      "
            + "                                                               "
            + "    <tr>                                                       "
            + "      <td width=\"5%\" class=\"td_left\">&nbsp;</td>           "
            + "      <td width=\"31%\" height=\"18\" class=\"td_left\">25     "
            + "        de                                                     "
            + "        dezembro</td>                                          "
            + "      <td width=\"18%\" class=\"td_left\">(                    "
            + "          Domingo                                              "
            + "        )                                                      "
            + "        </td>                                                  "
            + "      <td width=\"46%\" class=\"td_left\">Natal</td>           "
            + "    </tr>                                                      "
            + "                                                               "
            + "  </tbody></table>                                             "
            + "</body>                                                        "
            + "</html>                                                        ";

    @Before
    public void setUp() {
        EXPECTED0.put("VAR1", Arrays.asList("12345", "var2='abcd'"));
        EXPECTED0.put("VAR2", Arrays.asList("var2='abcd'"));

        EXPECTED1.put("DATAS", Arrays.asList(" ", "01 de janeiro", "( Sexta )",
                "Dia Mundial da Paz", " ", "08 de fevereiro", "( Segunda )",
                "Carnaval", " ", "09 de fevereiro", "( Terça )", "Carnaval",
                " ", "25 de março", "( Sexta )", "Sexta-Feira da Paixão", " ",
                "21 de abril", "( Quinta )", "Tiradentes", " ", "01 de maio",
                "( Domingo )", "Dia do Trabalho", " ", "26 de junho",
                "( Domingo )", "Corpus Christi", " ", "07 de setembro",
                "( Quarta )", "Independência do Brasil", " ", "12 de outubro",
                "( Quarta )", "Nossa Srª Aparecida", " ", "02 de novembro",
                "( Quarta )", "Finados", " ", "15 de novembro", "( Terça )",
                "Proclamação da República", " ", "25 de dezembro",
                "( Domingo )", "Natal"));
    }

    @After
    public void tearDown() {
        result.clear();
        rules.clear();
    }

    @Test
    public void test_extractCase0WithRegex() {

        rules.put("VAR1", "<p>(.+?)</p>");
        rules.put("VAR2", "<p>(var2='(:?.*)')</p>");

        result = DataExtractor.extractWithRegex(CASE0, rules);

        assertEquals("data wasn't extracted correctly", EXPECTED0.toString(),
                result.toString());

    }

    @Test
    public void test_extractCase0WithSelector() {

        rules.put("VAR1", "p");
        rules.put("VAR2", "p:not(:first-child)");

        result = DataExtractor.extractWithSelector(CASE0, rules);

        assertEquals("data wasn't extracted correctly", EXPECTED0.toString(),
                result.toString());

    }
    
    @Test
    public void test_extractCase1WithSelector() {

        rules.put("DATAS", "td");

        result = DataExtractor.extractWithSelector(CASE1, rules);

        assertEquals("data wasn't extracted correctly", EXPECTED1.toString(),
                result.toString());

    }
}
