package pl.polsl.skirentalservice.core.ssh;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "commands")
public class XMLSshCommands {
    @XmlElement(name = "create-mailbox")
    private String createMailbox;

    @XmlElement(name = "update-mailbox-password")
    private String updateMailboxPassword;

    @XmlElement(name = "delete-mailbox")
    private String deleteMailbox;

    @XmlElement(name = "set-mailbox-capacity")
    private String setMailboxCapacity;

    public String getCreateMailbox() {
        return createMailbox;
    }

    void setCreateMailbox(String createMailbox) {
        this.createMailbox = createMailbox;
    }

    public String getUpdateMailboxPassword() {
        return updateMailboxPassword;
    }

    void setUpdateMailboxPassword(String updateMailboxPassword) {
        this.updateMailboxPassword = updateMailboxPassword;
    }

    public String getDeleteMailbox() {
        return deleteMailbox;
    }

    void setDeleteMailbox(String deleteMailbox) {
        this.deleteMailbox = deleteMailbox;
    }

    public String getSetMailboxCapacity() {
        return setMailboxCapacity;
    }

    void setSetMailboxCapacity(String setMailboxCapacity) {
        this.setMailboxCapacity = setMailboxCapacity;
    }

    @Override
    public String toString() {
        return "{" +
            "createMailbox=" + createMailbox +
            ", updateMailboxPassword=" + updateMailboxPassword +
            ", deleteMailbox=" + deleteMailbox +
            ", setMailboxCapacity=" + setMailboxCapacity +
            '}';
    }
}
