CREATE TABLE organization.employee
(
    `id`               BIGINT NOT NULL AUTO_INCREMENT,
    `create_at`        DATETIME(6) DEFAULT NULL,
    `update_at`        DATETIME(6) DEFAULT NULL,
    `in_use`           BIT(1) NOT NULL,
    `name`             VARCHAR(255) DEFAULT NULL,
    `nickname`         VARCHAR(255) DEFAULT NULL,
    `phone_number`     VARCHAR(255) DEFAULT NULL,
    `position`         VARCHAR(255) DEFAULT NULL,
    `responsibilities` VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE organization.organization
(
    `id`                BIGINT NOT NULL AUTO_INCREMENT,
    `create_at`         DATETIME(6) DEFAULT NULL,
    `update_at`         DATETIME(6) DEFAULT NULL,
    `in_use`            BIT(1) NOT NULL,
    `organization_name` VARCHAR(255) DEFAULT NULL,
    `parent_id`         BIGINT       DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_organizationName` (`organization_name`),
    KEY                 `fk_organization_parentOrganization` (`parent_id`),
    CONSTRAINT `fk_organization_parentOrganization` FOREIGN KEY (`parent_id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE organization.organization_employee
(
    `id`              BIGINT NOT NULL AUTO_INCREMENT,
    `create_at`       DATETIME(6) DEFAULT NULL,
    `update_at`       DATETIME(6) DEFAULT NULL,
    `employee_id`     BIGINT DEFAULT NULL,
    `organization_id` BIGINT DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY               `fk_organizationEmployee_employee` (`employee_id`),
    KEY               `fk_organizationEmployee_organization` (`organization_id`),
    CONSTRAINT `fk_organizationEmployee_employee` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`),
    CONSTRAINT `fk_organizationEmployee_organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO organization.organization (id, in_use ,organization_name) VALUES (1,1, "조직도");