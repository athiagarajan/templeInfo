package com.example.templeinfo;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TempleInfoApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private WebPageFetcher webPageFetcher;
    @MockBean
    private TempleService templeService;

    @Test
    void contextLoads() {
    }

    @Test
    void getTempleInfoShouldReturnExtractedData() throws IOException {
        String mockHtml = "<html><body>" +
                // Name section
                "<span class=\"subhead\">Sri Chidambaram thillai natarajar temple</span>" +
                // Table structure for fields
                "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\" width=\"510\"><tbody><tr>" +
                "<td align=\"left\" width=\"10\" class=\"style7\"></td>" +
                "<td width=\"214\" align=\"left\" valign=\"middle\" class=\"style8\">Moolavar</td>" +
                "<td width=\"10\" align=\"left\" valign=\"middle\" class=\"subhead\">:</td>" +
                "<td width=\"276\" align=\"left\" valign=\"middle\" class=\"style5\"><span style=\"font-weight: normal;\" class=\"style5\" id=\"TMoolavarLabel\">Thirumoolanathar (moolataneshwarar, Sabanayagar, Kootha Perumal, vidangur, Thtchinmeru vidangur, ponanbala koothan)</span></td>" +
                "</tr><tr>" +
                "<td align=\"left\" class=\"subhead\">&nbsp;</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"style8\">Urchavar</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"subhead\">:</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"style5\"><span id=\"TPoojaiLabel0\">-</span></td>" +
                "</tr><tr>" +
                "<td align=\"left\" class=\"subhead\">&nbsp;</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"style8\"><span id=\"Label9\">Amman / Thayar</span></td>" +
                "<td align=\"left\" valign=\"middle\" class=\"subhead\">:</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"style5\">Umayambikai – Sivakama Sundari</td>" +
                "</tr><tr>" +
                "<td align=\"left\" class=\"subhead\">&nbsp;</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"style8\">Thala Virutcham</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"subhead\">:</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"style5\">Thillai</td>" +
                "</tr><tr>" +
                "<td align=\"left\" class=\"subhead\">&nbsp;</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"style8\"><span id=\"Label14\">Theertham</span></td>" +
                "<td align=\"left\" valign=\"middle\" class=\"subhead\">:</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"style5\">Shivaganga, Paramananda Koopam, Vyakrapada Teertham, Anantha Teertham, Nagacheri, Brahmma Teertham, Shivapriyai,Pulimedu, Kuyya Teertham, Tiruparkadal</td>" +
                "</tr><tr>" +
                "<td align=\"left\" class=\"subhead\">&nbsp;</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"style8\"><span id=\"Label10\">Agamam / Pooja</span></td>" +
                "<td align=\"left\" valign=\"middle\" class=\"subhead\">:</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"style5\">-</td>" +
                "</tr><tr>" +
                "<td align=\"left\" class=\"subhead\">&nbsp;</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"style8\">Old year</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"subhead\">:</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"style5\">1000-2000 years old</td>" +
                "</tr><tr>" +
                "<td align=\"left\" class=\"subhead\">&nbsp;</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"style8\">Historical Name</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"subhead\">:</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"style5\">Thillai</td>" +
                "</tr><tr>" +
                "<td align=\"left\" class=\"subhead\">&nbsp;</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"style8\">City</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"subhead\">:</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"style5\">Chidambaram</td>" +
                "</tr><tr>" +
                "<td align=\"left\" class=\"subhead\">&nbsp;</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"style8\"><span id=\"Label16\">District</span></td>" +
                "<td align=\"left\" valign=\"middle\" class=\"subhead\">:</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"style5\"><a class=\"navigation\" href=\"district_temple_en.php?id=624\">Cuddalore</a></td>" +
                "</tr><tr>" +
                "<td align=\"left\" class=\"subhead\">&nbsp;</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"style8\">State</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"subhead\">:</td>" +
                "<td align=\"left\" valign=\"middle\" class=\"style5\">Tamil Nadu</td>" +
                "</tr></tbody></table>" +
                "<table>" +
                "<tr><td><span class=\"topic\">Singers:</span></td></tr>" +
                "<tr><td class=\"newsdetails\">Mock Singers Content</td></tr>" +
                "<tr><td><span class=\"topic\">Festival:</span></td></tr>" +
                "<tr><td class=\"newsdetails\">Mock Festival Content</td></tr>" +
                "<tr><td><span class=\"topic\">General Information:</span></td></tr>" +
                "<tr><td class=\"newsdetails\">Mock General Information Content</td></tr>" +
                "<tr><td><span class=\"topic\">Address:</span></td></tr>" +
                "<tr><td class=\"newsdetails\">Mock Address Content</td></tr>" +
                "<tr><td><span class=\"topic\">Phone:</span></td></tr>" +
                "<tr><td class=\"newsdetails\">Mock Phone Content</td></tr>" +
                "<tr><td><span class=\"topic\">Opening Time:</span></td></tr>" +
                "<tr><td class=\"newsdetails\">Mock Opening Time Content</td></tr>" +
                "<tr><td><span class=\"topic\">Temple's Speciality:</span></td></tr>" +
                "<tr><td class=\"newsdetails\">Mock Speciality Content</td></tr>" +
                "<tr><td><span class=\"subhead\">Prayers:</span></td></tr>" +
                "<tr><td class=\"newsdetails\">Mock Prayers Content</td></tr>" +
                "<tr><td><span class=\"subhead\">Thanks giving:</span></td></tr>" +
                "<tr><td class=\"newsdetails\">Mock Thanks Giving Content</td></tr>" +
                "<tr><td><span class=\"subhead\">Greatness Of Temple:</span></td></tr>" +
                "<tr><td class=\"newsdetails\">Mock Greatness Content</td></tr>" +
                "<tr><td><span class=\"subhead\">Temple History:</span></td></tr>" +
                "<tr><td class=\"newsdetails\">Mock History Content</td></tr>" +
                "<tr><td><span class=\"subhead\">Special Features:</span></td></tr>" +
                "<tr><td class=\"newsdetails\">Mock Features Content</td></tr>" +
                "</table>" +
                "<input type=\"hidden\" name=\"hfLat\" id=\"hfLat\" value=\"11.40033684\" />" +
                "<input type=\"hidden\" name=\"hfLan\" id=\"hfLan\" value=\"79.69300032\" />" +
                "<table>" +
                "<tr><td><span class=\"subhead\">Location :</span><br><span class=\"newsdetails\">Mock Location Content</span></td></tr>" +
                "<tr><td><span class=\"subhead\">Near By Airport :</span><br><span class=\"newsdetails\">Mock Near By Airport Content</span></td></tr>" +
                "<tr><td><span class=\"subhead\">Near By Railway Station :</span><br><span class=\"newsdetails\">Mock Near By Railway Station Content</span></td></tr>" +
                "<tr><td><span class=\"subhead\">Accomodation :</span><br><span class=\"newsdetails\"><b>Cuddalore:</b><br><br>Hotel Sharadaram:&nbsp; +91-4144-221 336<br>Hotel Akshaya: +91-4144-220 191 and 192<br></span></td></tr>" +
                "</table>" +
                "</body></html>";

        when(webPageFetcher.fetch("https://temple.dinamalar.com/en/new_en.php?id=492")).thenReturn(Jsoup.parse(mockHtml));
        when(templeService.saveOrUpdateTemple(any(Temple.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TempleUrlRequest request = new TempleUrlRequest(492L);
        ResponseEntity<Temple> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/temple-info",
                request,
                Temple.class
        );

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Temple temple = response.getBody();

        assertThat(temple).isNotNull();
        assertThat(temple.id()).isEqualTo(492L);
        assertThat(temple.name()).isEqualTo("Sri Chidambaram thillai natarajar temple");
        assertThat(temple.moolavar()).isEqualTo("Thirumoolanathar (moolataneshwarar, Sabanayagar, Kootha Perumal, vidangur, Thtchinmeru vidangur, ponanbala koothan)");
        assertThat(temple.urchavar()).isEqualTo("-");
        assertThat(temple.ammanThayar()).isEqualTo("Umayambikai – Sivakama Sundari");
        assertThat(temple.thalaVirutcham()).isEqualTo("Thillai");
        assertThat(temple.theertham()).isEqualTo("Shivaganga, Paramananda Koopam, Vyakrapada Teertham, Anantha Teertham, Nagacheri, Brahmma Teertham, Shivapriyai,Pulimedu, Kuyya Teertham, Tiruparkadal");
        assertThat(temple.agamamPooja()).isEqualTo("-");
        assertThat(temple.oldYear()).isEqualTo("1000-2000 years old");
        assertThat(temple.historicalName()).isEqualTo("Thillai");
        assertThat(temple.city()).isEqualTo("Chidambaram");
        assertThat(temple.district()).isEqualTo("Cuddalore");
        assertThat(temple.state()).isEqualTo("Tamil Nadu");
        assertThat(temple.singers()).isEqualTo("Mock Singers Content");
        assertThat(temple.festival()).isEqualTo("Mock Festival Content");
        assertThat(temple.generalInformation()).isEqualTo("Mock General Information Content");
        assertThat(temple.address()).isEqualTo("Mock Address Content");
        assertThat(temple.phone()).isEqualTo("Mock Phone Content");
        assertThat(temple.openingTime()).isEqualTo("Mock Opening Time Content");
        assertThat(temple.speciality()).isEqualTo("Mock Speciality Content");
        assertThat(temple.prayers()).isEqualTo("Mock Prayers Content");
        assertThat(temple.thanksGiving()).isEqualTo("Mock Thanks Giving Content");
        assertThat(temple.greatness()).isEqualTo("Mock Greatness Content");
        assertThat(temple.history()).isEqualTo("Mock History Content");
        assertThat(temple.features()).isEqualTo("Mock Features Content");
        assertThat(temple.hfLat()).isEqualTo(11.40033684);
        assertThat(temple.hfLan()).isEqualTo(79.69300032);
        assertThat(temple.location()).isEqualTo("Mock Location Content");
        assertThat(temple.nearByAirport()).isEqualTo("Mock Near By Airport Content");
        assertThat(temple.nearByRailwayStation()).isEqualTo("Mock Near By Railway Station Content");
        assertThat(temple.accommodation()).isEqualTo("Cuddalore:, Hotel Sharadaram: +91-4144-221 336, Hotel Akshaya: +91-4144-220 191 and 192");
    }
}
