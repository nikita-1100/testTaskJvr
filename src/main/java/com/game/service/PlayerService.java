package com.game.service;


import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exceptions.Exception400;
import com.game.exceptions.Exception404;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.game.controller.PlayerOrder.ID;

@Service
public class PlayerService {
    public static Specification<Player> checkRetries(String name,
                                                     String title,
                                                     Race race,
                                                     Profession profession,
                                                     Long after,
                                                     Long before,
                                                     Boolean banned,
                                                     Integer minExperience,
                                                     Integer maxExperience,
                                                     Integer minLevel,
                                                     Integer maxLevel,
                                                     PlayerOrder order) {
        return new Specification<Player>() {
            @Override
            public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (name != null) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
                }
                if (title != null) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
                }
                if (race != null) {
                    predicates.add(criteriaBuilder.equal(root.get("race"), race.toString()));
                }
                if (profession != null) {
                    predicates.add(criteriaBuilder.equal(root.get("profession"), profession.toString()));
                }
                Date dateBefore = new Date(before);
                Date dateAfter = new Date(after);
                if (after != 0 && before != 0 && after < before) {
                    predicates.add(criteriaBuilder.between(root.get("birthday"), dateAfter, dateBefore));
                } else if (after != 0 && before == 0) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), dateAfter));
                } else if (after == 0 && before != 0) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), dateBefore));
                }

                if (banned != null)
                    predicates.add(criteriaBuilder.equal(root.get("banned"), banned));
                predicates.add(criteriaBuilder.between(root.get("experience"), minExperience, maxExperience));
                predicates.add(criteriaBuilder.between(root.get("level"), minLevel, maxLevel));

                switch (order) {
                    case ID:
                        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
                        break;
                    case NAME:
                        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("name")));
                        break;
                    case EXPERIENCE:
                        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("experience")));
                        break;
                    case BIRTHDAY:
                        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("birthday")));
                        break;
                    case LEVEL:
                        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("level")));
                        break;
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    @Autowired
    private PlayerRepository playerRepository;

    public Player create(Player player) {
        return playerRepository.save(player);
    }

    public List<Player> readAll(String name,
                                String title,
                                Race race,
                                Profession profession,
                                Long after,
                                Long before,
                                Boolean banned,
                                Integer minExperience,
                                Integer maxExperience,
                                Integer minLevel,
                                Integer maxLevel,
                                PlayerOrder order,
                                Integer pageNumber,
                                Integer pageSize) {
        Specification<Player> specification = Specification.where(checkRetries(name,title,race,profession,after,before,banned,minExperience,maxExperience,minLevel,maxLevel,order));
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Player> page = playerRepository.findAll(specification,pageable);
        return page.getContent();
    }

    public Player read(Long id) {
            Player player = playerRepository.findById(id).get();
            return player;
    }

    public Player update(Player player, Long id) {
        Player updatePlayer = read(id);
        if (player.getName()!=null){
            if (player.getName().length() > 0 && player.getName().length() < 13)
                updatePlayer.setName(player.getName());
            else
                throw new Exception400();
        }
        if (player.getTitle()!=null){
            if (player.getTitle().length() > 0 && player.getTitle().length() < 31)
                updatePlayer.setTitle(player.getTitle());
            else
                throw new Exception400();
        }
        if (player.getRace()!=null){
            if (player.getRace().length()>0)
                updatePlayer.setRace(player.getRace());
            else
                throw new Exception400();
        }
        if (player.getProfession()!=null){
            if (player.getProfession().length()>0)
                updatePlayer.setProfession(player.getProfession());
            else
                throw new Exception400();
        }
        if (player.getBirthday()!=null){
            if (player.getBirthday().getYear()>99 && player.getBirthday().getYear()<1101)
                updatePlayer.setBirthday(player.getBirthday());
            else
                throw new Exception400();
        }
        if (player.isBanned()!=null)
            updatePlayer.setBanned(player.isBanned());
        if (player.getExperience()!=null){
            if (player.getExperience()>=0 && player.getExperience()<10000001)
            {
                updatePlayer.setExperience(player.getExperience());
                updatePlayer.setLevel(((int) (Math.sqrt(2500 + 200 * updatePlayer.getExperience())) - 50) / 100);
                updatePlayer.setUntilNextLevel(50 * (updatePlayer.getLevel() + 1) * (updatePlayer.getLevel() + 2) - updatePlayer.getExperience());
            }
            else
                throw new Exception400();
        };
        return playerRepository.saveAndFlush(updatePlayer);
    }

    public boolean delete(Long id) {
        if (playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public int playersCount(String name,
                            String title,
                            Race race,
                            Profession profession,
                            Long after,
                            Long before,
                            Boolean banned,
                            Integer minExperience,
                            Integer maxExperience,
                            Integer minLevel,
                            Integer maxLevel) {
        Specification<Player> specification = Specification.where(checkRetries(name,title,race,profession,after,before,banned,minExperience,maxExperience,minLevel,maxLevel,ID));
        List<Player> players = playerRepository.findAll(specification);
        return players.size();
    }

    public void ifBadIdThrowException(Long id){
        if (id <= 0) {
            throw new Exception400();
        } else if (id > playerRepository.count() || !playerRepository.findById(id).isPresent()) {
            throw new Exception404();
        }
    }
}
