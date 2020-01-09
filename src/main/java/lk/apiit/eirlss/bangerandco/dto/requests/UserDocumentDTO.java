package lk.apiit.eirlss.bangerandco.dto.requests;

import lk.apiit.eirlss.bangerandco.models.UserDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDocumentDTO {
    private String type;
    private String path;

    public UserDocument transformToEntity() {
        UserDocument document = new UserDocument();
        document.setDocumentType(this.type);
        document.setDocumentPath(this.path);
        return document;
    }
}
