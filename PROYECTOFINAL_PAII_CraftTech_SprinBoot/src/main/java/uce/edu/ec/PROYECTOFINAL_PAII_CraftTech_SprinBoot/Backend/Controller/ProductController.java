/**
 * @author KevinPozo
 * @author BrayanLoya
 * @author JordyChamba
 * Title: CraftTech Fabrication System (BACKEND)
 * */
package uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Service.MaterialService;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Service.ProductService;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Model.Product;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private MaterialService materialService;

    @PostMapping("/create")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        if (product == null || product.getName() == null || product.getMaterialId() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        if (materialService.findById(product.getMaterialId()) == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Product savedProduct = productService.save(product);
        return ResponseEntity.ok(savedProduct);
    }

    @GetMapping
    public ResponseEntity<Iterable<Product>> getAllProducts() {
        Iterable<Product> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/material/{materialId}")
    public ResponseEntity<Iterable<Product>> getProductsByMaterialId(@PathVariable Long materialId) {
        Iterable<Product> products = productService.findByMaterialId(materialId);
        return ResponseEntity.ok(products);
    }
}
