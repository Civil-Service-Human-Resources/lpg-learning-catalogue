package uk.gov.cslearning.catalogue.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.catalogue.api.models.BulkUpdateResponse;
import uk.gov.cslearning.catalogue.api.models.IdsDto;
import uk.gov.cslearning.catalogue.api.models.PageableParams;
import uk.gov.cslearning.catalogue.api.models.SimplePage;
import uk.gov.cslearning.catalogue.domain.LearningTagHyperlinkDto;
import uk.gov.cslearning.catalogue.service.LearningTagService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/learning-tags/{learningTagId}/hyperlinks")
public class LearningTagHyperlinkController {

    private final LearningTagService learningTagService;

    public LearningTagHyperlinkController(LearningTagService learningTagService) {
        this.learningTagService = learningTagService;
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public SimplePage<LearningTagHyperlinkDto> getHyperlinksByTag(@PathVariable Long learningTagId, PageableParams pageable) {
        return learningTagService.getHyperlinksByLearningTagId(learningTagId, pageable.getAsPageable());
    }

    @GetMapping("/{hyperlinkId}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public LearningTagHyperlinkDto getHyperlink(@PathVariable Long learningTagId, @PathVariable Long hyperlinkId) {
        return learningTagService.getLearningTagHyperlink(learningTagId, hyperlinkId);
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public LearningTagHyperlinkDto createHyperlink(@PathVariable Long learningTagId, @Valid @RequestBody LearningTagHyperlinkDto dto) {
        return learningTagService.createLearningTagHyperlink(learningTagId, dto);
    }

    @PutMapping("/{hyperlinkId}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public LearningTagHyperlinkDto updateHyperlink(@PathVariable Long learningTagId, @PathVariable Long hyperlinkId,
                                                   @Valid @RequestBody LearningTagHyperlinkDto dto) {
        return learningTagService.updateLearningTagHyperlink(learningTagId, hyperlinkId, dto);
    }

    @DeleteMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public BulkUpdateResponse<Long> removeHyperlinksFromTag(@PathVariable Long learningTagId, @RequestBody IdsDto<Long> hyperlinkIdsDto) {
        return learningTagService.removeHyperlinksFromLearningTag(learningTagId, hyperlinkIdsDto);
    }

}
