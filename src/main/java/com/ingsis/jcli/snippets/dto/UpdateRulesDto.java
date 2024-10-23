package com.ingsis.jcli.snippets.dto;

import com.ingsis.jcli.snippets.common.Generated;
import com.ingsis.jcli.snippets.models.Rule;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Generated
@Data
public class UpdateRulesDto {
  @NotNull(message = "Has to specific a userId")
  private String userId;

  @NotNull(message = "Has to specific the rules to be set")
  private List<Rule> rules;
}
