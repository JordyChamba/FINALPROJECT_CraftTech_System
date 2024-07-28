/**
 * @author KevinPozo
 * @author BrayanLoya
 * @author JordyChamba
 * Title: CraftTech Fabrication System (BACKEND)
 * */
package uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Model.Material;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
}
