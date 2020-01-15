# prtg-aliyun
PRTG Adapter for Alibaba Cloud / Aliyun Cloud Monitor

## Getting Started

### Prerequisites

- PRTG 17 or Above
- Java SE Runtime 1.6
- Access Key ID & Secret with RAM Cloud Monitor Read Only

### Deployment

Use PRTG EXE/Script Sensor

## Configuration

### Sensor Argument

The Application takes 7 / 9 / 11 Arguments in Following Order:

1. Endpoint (Region ID)
2. Project (Alibaba Cloud Product Identification in Preset Metric)
3. Metric
4. Access Key ID
5. Access Key Secret
6. Statistical Period (Second)
7. Instance ID
8. Additional Dimension Name
9. Additional Dimension Value
10. Additional Dimension Name
11. Additional Dimension Value

## Version

### Build Version 2.5.1
Add Secure Acceleration and Global Acceleration in Preset Metric

### Build Version 2.5 (Initial Commit)
With Alibaba Cloud Preset Metric (https://www.alibabacloud.com/help/doc-detail/28619.htm)
