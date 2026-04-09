package io.hexlet.spring;

import io.hexlet.spring.model.Post;
import io.hexlet.spring.model.Tag;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.PostRepository;
import io.hexlet.spring.repository.TagRepository;
import io.hexlet.spring.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ModelGenerator {

    private final Faker faker;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public ModelGenerator(Faker faker,
                          UserRepository userRepository,
                          PostRepository postRepository,
                          TagRepository tagRepository) {
        this.faker = faker;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    @PostConstruct
    public void generateData() {
        List<Tag> tagList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            var tag = new Tag();
            tag.setName(faker.lorem().word());
            tagRepository.save(tag);
            tagList.add(tag);
        }

        for (int i = 0; i < 5; i++) {
            var user = new User();
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            user.setEmail(faker.internet().emailAddress());
            userRepository.save(user);

            var post = new Post();
            post.setTitle(faker.book().title());
            post.setContent(faker.lorem().paragraph());
            post.setPublished(faker.bool().bool());
            post.setUser(user);
            List<Tag> tags = new ArrayList<>(tagList);
            Collections.shuffle(tags);
            post.setTags(tags.stream().limit(5).toList());
            postRepository.save(post);

        }
    }
}
