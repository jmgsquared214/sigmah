package org.sigmah.server.domain.value;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = "file_meta")
public class File implements Serializable {

	private static final long serialVersionUID = -271699094058979365L;

	private Long id;
	private String name;
	private Date removedDate;
	private List<FileVersion> versions = new ArrayList<FileVersion>();

	public void setId(Long id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_file")
	public Long getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "name", nullable = false, length = 4096)
	public String getName() {
		return name;
	}

	public void setRemovedDate(Date removedDate) {
		this.removedDate = removedDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "removed_date", nullable = true)
	public Date getRemovedDate() {
		return removedDate;
	}

	public void setVersions(List<FileVersion> versions) {
		this.versions = versions;
	}

	@OneToMany(mappedBy = "parentFile", cascade = CascadeType.ALL)
	public List<FileVersion> getVersions() {
		return versions;
	}

//	/**
//	 * Adds a dummy new version of the current file.
//	 *
//	 * @param currentUser
//	 *            The author.
//	 */
//	public void addVersion(User currentUser) {
//
//		final FileVersion version = new FileVersion();
//		version.setAddedDate(new Date());
//		version.setAuthor(currentUser);
//		version.setPath("/somewhere/on/the/hdd");
//		version.setSize(1024L);
//		version.setVersionNumber(DummyService.getNextFileVersionNumber(this));
//
//		version.setParentFile(this);
//		versions.add(version);
//	}

}
