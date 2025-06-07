import clsx from "clsx";

interface ServiceCardProps {
  title: string;
  description: string;
  tags?: string[];
  className?: string;
}

const ServiceCard = ({
  title,
  description,
  tags = [],
  className = "",
}: ServiceCardProps) => {
  return (
    <div className={clsx("bg-white rounded-lg p-6", className)}>
      <div className="space-y-4">
        <h3 className="text-xl font-semibold text-gray-900">{title}</h3>
        <p className="text-gray-600">{description}</p>
        {tags.length > 0 && (
          <div className="flex flex-wrap gap-2">
            {tags.map((tag, index) => (
              <span
                key={index}
                className="text-xs font-medium text-primary px-3 py-1 bg-primary/5 rounded-full"
              >
                {tag}
              </span>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default ServiceCard;
