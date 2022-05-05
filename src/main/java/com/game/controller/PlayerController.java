package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/rest/players")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/count")
    public int getPlayersCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false, defaultValue = "0") Long after,
            @RequestParam(value = "before", required = false, defaultValue = "0") Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false, defaultValue = "0") Integer minExperience,
            @RequestParam(value = "maxExperience", required = false, defaultValue = "2147483647") Integer maxExperience,
            @RequestParam(value = "minLevel", required = false, defaultValue = "0") Integer minLevel,
            @RequestParam(value = "maxLevel", required = false, defaultValue = "2147483647") Integer maxLevel) {
        return playerService.playersCount(name,
                title,
                race,
                profession,
                after,
                before,
                banned,
                minExperience,
                maxExperience,
                minLevel,
                maxLevel);
    }

    @GetMapping
    public List<Player> read(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false, defaultValue = "0") Long after,
            @RequestParam(value = "before", required = false, defaultValue = "0") Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false, defaultValue = "0") Integer minExperience,
            @RequestParam(value = "maxExperience", required = false, defaultValue = "2147483647") Integer maxExperience,
            @RequestParam(value = "minLevel", required = false, defaultValue = "0") Integer minLevel,
            @RequestParam(value = "maxLevel", required = false, defaultValue = "2147483647") Integer maxLevel,
            @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {
        return playerService.readAll(name,
                title,
                race,
                profession,
                after,
                before,
                banned,
                minExperience,
                maxExperience,
                minLevel,
                maxLevel,
                order,
                pageNumber,
                pageSize);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getByID(@PathVariable Long id) {
        playerService.ifBadIdThrowException(id);
            return new ResponseEntity<>(playerService.read(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        if (player.getName()==null || player.getTitle()==null || player.getRace()==null ||
                player.getProfession()==null || player.getBirthday()==null || player.getExperience()==null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (player.getName().length() > 0 &&player.getName().length() < 13 &&
                player.getTitle().length() > 0 && player.getTitle().length() < 31 &&
                player.getRace().length()>0 &&
                player.getProfession().length()>0 &&
                player.getBirthday().getYear()>99 && player.getBirthday().getYear()<1101 &&
                player.getExperience()>=0 && player.getExperience()<10000001
        ) {
            player.setLevel((((int) (Math.sqrt(2500 + 200 * player.getExperience())) - 50) / 100));
            player.setUntilNextLevel((50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience()));
            return new ResponseEntity<>(playerService.create(player), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@RequestBody Player player, @PathVariable(required = false) Long id) {
        playerService.ifBadIdThrowException(id);
        return new ResponseEntity<>(playerService.update(player,id), HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Player> deleteById(@PathVariable Long id) {
        playerService.ifBadIdThrowException(id);
        playerService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

