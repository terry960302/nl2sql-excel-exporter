import React, { useState } from "react";
import styled from "styled-components";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

const Form = styled.form`
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
`;

const FormGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
`;

const ButtonGroup = styled.div`
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
`;

export interface DatasourceFormData {
  id: string;
  name: string;
  dbType: string;
  engineType: string;
  description: string;
}

interface NewDatasourceFormProps {
  onSubmit: (data: DatasourceFormData) => void;
  onTestConnection: (data: DatasourceFormData) => void;
}

export const NewDatasourceForm: React.FC<NewDatasourceFormProps> = ({
  onSubmit,
  onTestConnection,
}) => {
  const [formData, setFormData] = useState<DatasourceFormData>({
    id: "",
    name: "",
    dbType: "",
    engineType: "",
    description: "",
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  const handleTestConnection = (e: React.FormEvent) => {
    e.preventDefault();
    onTestConnection(formData);
  };

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSelectChange = (name: string, value: string) => {
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  return (
    <Form onSubmit={handleSubmit}>
      <FormGroup>
        <Label htmlFor="name">별칭</Label>
        <Input
          id="name"
          name="name"
          value={formData.name}
          onChange={handleChange}
          required
        />
      </FormGroup>

      <FormGroup>
        <Label htmlFor="dbType">데이터베이스 타입</Label>
        <Select
          value={formData.dbType}
          onValueChange={(value) => handleSelectChange("dbType", value)}
        >
          <SelectTrigger>
            <SelectValue placeholder="데이터베이스 타입을 선택하세요" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="RDB">Relational Database</SelectItem>
            <SelectItem value="NOSQL">NoSQL Database</SelectItem>
          </SelectContent>
        </Select>
      </FormGroup>

      <FormGroup>
        <Label htmlFor="engineType">DB엔진 타입</Label>
        <Select
          value={formData.engineType}
          onValueChange={(value) => handleSelectChange("engineType", value)}
        >
          <SelectTrigger>
            <SelectValue placeholder="엔진 타입을 선택하세요" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="MYSQL">MySQL</SelectItem>
            <SelectItem value="POSTGRES">PostgreSQL</SelectItem>
            <SelectItem value="ORACLE">Oracle</SelectItem>
          </SelectContent>
        </Select>
      </FormGroup>

      <FormGroup>
        <Label htmlFor="description">설명</Label>
        <Input
          id="description"
          name="description"
          value={formData.description}
          onChange={handleChange}
        />
      </FormGroup>

      <ButtonGroup>
        <Button type="button" variant="outline" onClick={handleTestConnection}>
          연결 테스트
        </Button>
        <Button type="submit">저장</Button>
      </ButtonGroup>
    </Form>
  );
};
