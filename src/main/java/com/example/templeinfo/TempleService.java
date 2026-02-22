package com.example.templeinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class TempleService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TempleService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Temple saveOrUpdateTemple(Temple temple) {
        String checkSql = "SELECT COUNT(*) FROM temples WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, temple.id());

        if (count != null && count > 0) {
            return updateTemple(temple);
        } else {
            return insertTemple(temple);
        }
    }

    private Temple insertTemple(Temple temple) {
        String sql = "INSERT INTO temples (id, name, moolavar, urchavar, amman_thayar, thala_virutcham, theertham, agamam_pooja, old_year, historical_name, city, district, state, singers, festival, general_information, address, phone, opening_time, speciality, prayers, thanks_giving, greatness, history, features, hf_lat, hf_lan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                temple.id(), temple.name(), temple.moolavar(), temple.urchavar(), temple.ammanThayar(),
                temple.thalaVirutcham(), temple.theertham(), temple.agamamPooja(), temple.oldYear(),
                temple.historicalName(), temple.city(), temple.district(), temple.state(),
                temple.singers(), temple.festival(), temple.generalInformation(), temple.address(),
                temple.phone(), temple.openingTime(), temple.speciality(), temple.prayers(),
                temple.thanksGiving(), temple.greatness(), temple.history(), temple.features(),
                temple.hfLat(), temple.hfLan());
        return temple;
    }

    private Temple updateTemple(Temple temple) {
        String sql = "UPDATE temples SET name = ?, moolavar = ?, urchavar = ?, amman_thayar = ?, thala_virutcham = ?, theertham = ?, agamam_pooja = ?, old_year = ?, historical_name = ?, city = ?, district = ?, state = ?, singers = ?, festival = ?, general_information = ?, address = ?, phone = ?, opening_time = ?, speciality = ?, prayers = ?, thanks_giving = ?, greatness = ?, history = ?, features = ?, hf_lat = ?, hf_lan = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                temple.name(), temple.moolavar(), temple.urchavar(), temple.ammanThayar(),
                temple.thalaVirutcham(), temple.theertham(), temple.agamamPooja(), temple.oldYear(),
                temple.historicalName(), temple.city(), temple.district(), temple.state(),
                temple.singers(), temple.festival(), temple.generalInformation(), temple.address(),
                temple.phone(), temple.openingTime(), temple.speciality(), temple.prayers(),
                temple.thanksGiving(), temple.greatness(), temple.history(), temple.features(),
                temple.hfLat(), temple.hfLan(), temple.id());
        return temple;
    }
}
