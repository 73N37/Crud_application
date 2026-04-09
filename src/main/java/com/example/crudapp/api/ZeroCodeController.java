package com.example.crudapp.api;

import com.example.crudapp.data.core.meta.VirtualDocument;
import com.example.crudapp.logic.VirtualCrudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * [INTERFACE LAYER]
 * The Zero-Code Proxy.
 * Handles dynamic data-driven APIs registered in the database.
 */
@RestController
@RequestMapping("/api/v3")
public class ZeroCodeController {

    private static final Logger log = LoggerFactory.getLogger(ZeroCodeController.class);
    private final VirtualCrudService virtualService;

    public ZeroCodeController(VirtualCrudService virtualService) {
        this.virtualService = virtualService;
    }

    @GetMapping("/{resource}")
    public ResponseEntity<List<Map<String, Object>>> getAll(@PathVariable String resource) {
        log.info("🌐 Zero-Code: GET /api/v3/{}", resource);
        List<Map<String, Object>> results = virtualService.getAll(resource).stream()
                .map(doc -> {
                    Map<String, Object> map = doc.getData();
                    map.put("_id", doc.getId());
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{resource}/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable String resource, @PathVariable Long id) {
        log.info("🌐 Zero-Code: GET /api/v3/{}/{}", resource, id);
        return virtualService.getById(resource, id)
                .map(doc -> {
                    Map<String, Object> map = doc.getData();
                    map.put("_id", doc.getId());
                    return ResponseEntity.ok(map);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{resource}")
    public ResponseEntity<Map<String, Object>> create(@PathVariable String resource, @RequestBody Map<String, Object> body) {
        log.info("🌐 Zero-Code: POST /api/v3/{} (body: {})", resource, body);
        VirtualDocument doc = virtualService.save(resource, body);
        Map<String, Object> response = doc.getData();
        response.put("_id", doc.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{resource}/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable String resource, @PathVariable Long id, @RequestBody Map<String, Object> body) {
        log.info("🌐 Zero-Code: PUT /api/v3/{}/{} (body: {})", resource, id, body);
        VirtualDocument doc = virtualService.update(id, body);
        Map<String, Object> response = doc.getData();
        response.put("_id", doc.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{resource}/{id}")
    public ResponseEntity<Void> delete(@PathVariable String resource, @PathVariable Long id) {
        log.info("🌐 Zero-Code: DELETE /api/v3/{}/{}", resource, id);
        virtualService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
