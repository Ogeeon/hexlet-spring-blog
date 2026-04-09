package io.hexlet.spring.controller;

import io.hexlet.spring.dto.TagCreateDTO;
import io.hexlet.spring.dto.TagDTO;
import io.hexlet.spring.dto.TagUpdateDTO;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.mapper.TagMapper;
import io.hexlet.spring.repository.TagRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagsController {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public TagsController(TagRepository tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TagDTO> index() {
        return tagRepository.findAll().stream().map(tagMapper::map).toList();
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagDTO show(@PathVariable Long id) {
        return tagRepository.findById(id)
                .map(tagMapper::map)
                .orElseThrow(() -> new ResourceNotFoundException("Tag with ID " + id + " not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagDTO create(@Valid @RequestBody TagCreateDTO dto) {
        var tag = tagMapper.map(dto);
        tagRepository.save(tag);
        return tagMapper.map(tag);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagDTO update(@PathVariable Long id, @Valid @RequestBody TagUpdateDTO dto) {
        var tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag with ID " + id + " not found"));
        tagMapper.update(dto, tag);
        var saved = tagRepository.save(tag);
        return tagMapper.map(saved);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        tagRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tag with ID " + id + " no found"));
        tagRepository.deleteById(id);
    }
}
